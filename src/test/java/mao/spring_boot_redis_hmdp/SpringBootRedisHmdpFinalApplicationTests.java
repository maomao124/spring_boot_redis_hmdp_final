package mao.spring_boot_redis_hmdp;

import lombok.extern.slf4j.Slf4j;
import mao.spring_boot_redis_hmdp.entity.Shop;
import mao.spring_boot_redis_hmdp.service.IShopService;
import mao.spring_boot_redis_hmdp.utils.RedisConstants;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@SpringBootTest
class SpringBootRedisHmdpFinalApplicationTests
{

    @Resource
    private IShopService shopService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void contextLoads()
    {
    }

    /**
     * 加载店铺信息到redis，按类型加载
     */
    @Test
    void load()
    {
        //查询店铺信息
        List<Shop> list = shopService.list();
        //店铺分组，放入到一个集合中
        Map<Long, List<Shop>> collect = list.stream().collect(Collectors.groupingBy(Shop::getTypeId));
        //分批写入redis
        for (Long typeId : collect.keySet())
        {
            //值
            List<Shop> shops = collect.get(typeId);
            List<RedisGeoCommands.GeoLocation<String>> locations = new ArrayList<>(shops.size());
            for (Shop shop : shops)
            {
                locations.add(new RedisGeoCommands.GeoLocation<>
                        (shop.getId().toString(), new Point(shop.getX(), shop.getY())));
            }
            //写入redis
            stringRedisTemplate.opsForGeo().add(RedisConstants.SHOP_GEO_KEY + typeId, locations);
        }
    }

    /**
     * 测试redis的uv统计功能
     */
    @Test
    void UV_statistics()
    {
        //发送单位,当前为1000条发一次，如果每次都发送会大大增加网络io
        int length = 1000;
        //发送的总数，当前为一百万条数据
        int total = 1000000;
        int j = 0;
        String[] values = new String[length];
        for (int i = 0; i < total; i++)
        {
            j = i % length;
            //赋值
            values[j] = "user_" + i;
            if (j == length - 1)
            {
                //发送到redis
                stringRedisTemplate.opsForHyperLogLog().add("UV", values);
            }
        }
        //发送完成，获得数据
        Long size = stringRedisTemplate.opsForHyperLogLog().size("UV");
        log.info("统计结果：" + size);
        //统计结果：997593
        //统计结果：1998502(两百万)
    }

}
