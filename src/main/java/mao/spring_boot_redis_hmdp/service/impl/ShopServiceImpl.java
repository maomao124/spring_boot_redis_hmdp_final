package mao.spring_boot_redis_hmdp.service.impl;


import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import mao.spring_boot_redis_hmdp.dto.RedisData;
import mao.spring_boot_redis_hmdp.dto.Result;
import mao.spring_boot_redis_hmdp.entity.Shop;
import mao.spring_boot_redis_hmdp.mapper.ShopMapper;
import mao.spring_boot_redis_hmdp.service.IShopService;
import mao.spring_boot_redis_hmdp.utils.RedisConstants;
import mao.spring_boot_redis_hmdp.utils.RedisUtils;
import mao.spring_boot_redis_hmdp.utils.SystemConstants;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService
{

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedisUtils redisUtils;


    @Override
    public Result queryShopById(Long id)
    {
        //查询
        //Shop shop = this.queryWithMutex(id);
        //Shop shop = this.queryWithLogicalExpire(id);
        Shop shop = redisUtils.query(RedisConstants.CACHE_SHOP_KEY, RedisConstants.LOCK_SHOP_KEY, id, Shop.class, this::getById,
                RedisConstants.CACHE_SHOP_TTL, TimeUnit.MINUTES, 300);
        //判断
        if (shop == null)
        {
            //不存在
            return Result.fail("店铺信息不存在");
        }
        //返回
        return Result.ok(shop);
    }

    /**
     * 互斥锁解决缓存击穿问题
     *
     * @param id 商铺id
     * @return Shop
     */
    private Shop queryWithMutex(Long id)
    {
        //获取redisKey
        String redisKey = RedisConstants.CACHE_SHOP_KEY + id;
        //从redis中查询商户信息，根据id
        String shopJson = stringRedisTemplate.opsForValue().get(redisKey);
        //判断取出的数据是否为空
        if (StrUtil.isNotBlank(shopJson))
        {
            //不是空，redis里有，返回
            return JSONUtil.toBean(shopJson, Shop.class);
        }
        //是空串，不是null，返回
        if (shopJson != null)
        {
            return null;
        }
        //锁的key
        String lockKey = RedisConstants.LOCK_SHOP_KEY + id;

        Shop shop = null;
        try
        {
            //获取互斥锁
            boolean lock = tryLock(lockKey);
            //判断锁是否获取成功
            if (!lock)
            {
                //没有获取到锁
                //200毫秒后再次获取
                Thread.sleep(200);
                //递归调用
                return queryWithMutex(id);
            }
            //得到了锁
            //null，查数据库
            shop = this.getById(id);
            //判断数据库里的信息是否为空
            if (shop == null)
            {
                //空，将空值写入redis，返回错误
                stringRedisTemplate.opsForValue().set(redisKey, "", RedisConstants.CACHE_NULL_TTL, TimeUnit.MINUTES);
                return null;
            }
            //存在，回写到redis里，设置随机的过期时间
            stringRedisTemplate.opsForValue().set(redisKey, JSONUtil.toJsonStr(shop),
                    RedisConstants.CACHE_SHOP_TTL * 60 + getIntRandom(0, 300), TimeUnit.SECONDS);
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            //释放锁
            //System.out.println("释放锁");
            this.unlock(lockKey);
        }
        //返回数据
        return shop;
    }

    //线程池
    private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);

    /**
     * 使用逻辑过期解决缓存击穿问题
     *
     * @param id 商铺id
     * @return Shop
     */
    private Shop queryWithLogicalExpire(Long id)
    {
        //获取redisKey
        String redisKey = RedisConstants.CACHE_SHOP_KEY + id;
        //从redis中查询商户信息，根据id
        String shopJson = stringRedisTemplate.opsForValue().get(redisKey);
        //判断取出的数据是否为空
        if (StrUtil.isBlank(shopJson))
        {
            //是空，redis里没有，返回
            return null;
        }

        //json转类
        RedisData redisData = JSONUtil.toBean(shopJson, RedisData.class);
        //获取过期时间
        LocalDateTime expireTime = redisData.getExpireTime();
        //获取商铺信息
        Shop shop = JSONUtil.toBean((JSONObject) redisData.getData(), Shop.class);
        //判断是否过期
        if (expireTime.isAfter(LocalDateTime.now()))
        {
            //没有过期，返回
            return shop;
        }
        //已经过期，缓存重建
        //获取互斥锁
        String lockKey = RedisConstants.LOCK_SHOP_KEY + id;
        boolean isLock = tryLock(lockKey);
        if (isLock)
        {
            //获取锁成功
            // 开辟独立线程
            CACHE_REBUILD_EXECUTOR.submit(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        saveShop2Redis(id, 20L);
                    }
                    catch (InterruptedException e)
                    {
                        throw new RuntimeException(e);
                    }
                    finally
                    {
                        //释放锁
                        unlock(lockKey);
                    }
                }
            });
        }
        //没有获取到锁，使用旧数据返回
        return shop;

    }

    /**
     * 保存商铺信息到redis
     *
     * @param id            商铺的id
     * @param expireSeconds 过期的时间，单位是秒
     * @throws InterruptedException 异常
     */
    public void saveShop2Redis(Long id, Long expireSeconds) throws InterruptedException
    {
        // 查询数据库
        Shop shop = getById(id);
        // 封装缓存过期时间
        RedisData redisData = new RedisData();
        redisData.setData(shop);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(expireSeconds));
        //保存到redis
        stringRedisTemplate.opsForValue().set(RedisConstants.CACHE_SHOP_KEY, JSONUtil.toJsonStr(redisData));
    }


    @Override
    public Result updateShop(Shop shop)
    {
        //获得id
        Long id = shop.getId();
        //判断是否为空
        if (id == null)
        {
            return Result.fail("商户id不能为空");
        }
        //不为空
        //先更新数据库
        boolean b = this.updateById(shop);
        //更新失败，返回
        if (!b)
        {
            return Result.fail("更新失败");
        }
        //更新没有失败
        //删除redis里的数据，下一次查询时自动添加进redis
        //redisKey
        String redisKey = RedisConstants.CACHE_SHOP_KEY + id;
        stringRedisTemplate.delete(redisKey);
        //返回响应
        return Result.ok();
    }

    @Override
    public Result queryShopByType(Integer typeId, Integer current, Double x, Double y)
    {
        //判断是否传递了坐标轴信息，如果没有传递，基本分页
        if (x == null || y == null)
        {
            // 根据类型分页查询
            Page<Shop> page = this.query()
                    .eq("type_id", typeId)
                    .page(new Page<>(current, SystemConstants.DEFAULT_PAGE_SIZE));
            // 返回数据
            return Result.ok(page.getRecords());
        }
        //传递了坐标信息
        int from = (current - 1) * SystemConstants.DEFAULT_PAGE_SIZE;
        int end = current * SystemConstants.DEFAULT_PAGE_SIZE;
        //按距离排序且分页 GeoSearchCommandArgs.newGeoSearchArgs().includeDistance().limit(end))
        GeoResults<RedisGeoCommands.GeoLocation<String>> geoResults = stringRedisTemplate.
                opsForGeo().geoRadius(RedisConstants.SHOP_GEO_KEY + typeId,
                        new Circle(new Point(x, y), 5000),
                        RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeDistance().limit(end));
        //判断是否为空
        if (geoResults == null)
        {
            //返回空集合
            return Result.ok(Collections.emptyList());
        }
        //不为空
        //获取内容
        List<GeoResult<RedisGeoCommands.GeoLocation<String>>> content = geoResults.getContent();
        //判断是否到底
        if (from >= content.size())
        {
            return Result.ok(Collections.emptyList());
        }
        List<Long> ids = new ArrayList<>(content.size());
        Map<String, Distance> distanceMap = new HashMap<>(content.size());
        //截取from到end的部分
        content.stream().skip(from).forEach(result ->
        {
            //获取店铺的id
            String id = result.getContent().getName();
            //加入到集合中
            ids.add(Long.valueOf(id));
            //获得距离信息
            Distance distance = result.getDistance();
            //加入到map集合里
            distanceMap.put(id, distance);
            //System.out.println(id+"-----"+distance.getValue());
        });
        //拼接
        String join = StrUtil.join(",", ids);
        //查询数据库
        List<Shop> shops = this.query().in("id", ids).last("order by field(id," + join + ")").list();
        //填充距离信息
        for (Shop shop : shops)
        {
            shop.setDistance(distanceMap.get(shop.getId().toString()).getValue());
        }
        //返回
        return Result.ok(shops);
    }

    /**
     * 获取一个随机数，区间包含min和max
     *
     * @param min 最小值
     * @param max 最大值
     * @return int 型的随机数
     */
    @SuppressWarnings("all")
    private int getIntRandom(int min, int max)
    {
        if (min > max)
        {
            min = max;
        }
        return min + (int) (Math.random() * (max - min + 1));
    }

    /**
     * 获取锁
     *
     * @param key redisKey
     * @return 获取锁成功，返回true，否则返回false
     */
    private boolean tryLock(String key)
    {
        Boolean result = stringRedisTemplate.opsForValue().setIfAbsent(key, "1",
                RedisConstants.LOCK_SHOP_TTL, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(result);
    }

    /**
     * 释放锁
     *
     * @param key redisKey
     */
    private void unlock(String key)
    {
        stringRedisTemplate.delete(key);
    }

}
