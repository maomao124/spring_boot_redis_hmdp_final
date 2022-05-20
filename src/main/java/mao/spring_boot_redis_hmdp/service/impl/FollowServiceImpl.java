package mao.spring_boot_redis_hmdp.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import mao.spring_boot_redis_hmdp.dto.Result;
import mao.spring_boot_redis_hmdp.dto.UserDTO;
import mao.spring_boot_redis_hmdp.entity.Follow;
import mao.spring_boot_redis_hmdp.entity.User;
import mao.spring_boot_redis_hmdp.mapper.FollowMapper;
import mao.spring_boot_redis_hmdp.service.IFollowService;
import mao.spring_boot_redis_hmdp.service.IUserService;
import mao.spring_boot_redis_hmdp.utils.RedisConstants;
import mao.spring_boot_redis_hmdp.utils.UserHolder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements IFollowService
{

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private IUserService userService;

/*    @Override
    public Result follow(Long followUserId, Boolean isFollow)
    {
        //空值判断
        if (followUserId == null)
        {
            return Result.fail("关注的用户id不能为空");
        }
        if (isFollow == null)
        {
            return Result.fail("参数异常");
        }
        //获取当前用户的id
        Long userID = UserHolder.getUser().getId();
        //判断是关注还是取消关注
        if (isFollow)
        {
            //是关注
            //加关注
            Follow follow = new Follow();
            //设置关注的用户id
            follow.setFollowUserId(followUserId);
            //设置当前用户的id
            follow.setUserId(userID);
            //保存到数据库
            boolean save = this.save(follow);
            //判断是否关注失败
            if (!save)
            {
                return Result.fail("关注失败");
            }
        }
        else
        {
            //不是关注，取消关注
            //删除数据库里的相关信息
            QueryWrapper<Follow> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("follow_user_id", followUserId).eq("user_id", userID);
            //删除
            boolean remove = this.remove(queryWrapper);
            if (!remove)
            {
                return Result.fail("取消关注失败");
            }
        }
        //返回
        return Result.ok();
    }*/

    @Override
    public Result follow(Long followUserId, Boolean isFollow)
    {
        //空值判断
        if (followUserId == null)
        {
            return Result.fail("关注的用户id不能为空");
        }
        if (isFollow == null)
        {
            return Result.fail("参数异常");
        }
        //获取当前用户的id
        Long userID = UserHolder.getUser().getId();
        //判断是关注还是取消关注
        if (isFollow)
        {
            //是关注
            //加关注
            Follow follow = new Follow();
            //设置关注的用户id
            follow.setFollowUserId(followUserId);
            //设置当前用户的id
            follow.setUserId(userID);
            //保存到数据库
            boolean save = this.save(follow);
            //判断是否关注失败
            if (!save)
            {
                return Result.fail("关注失败");
            }
            //关注成功，保存到redis
            //登录用户的key
            String redisUserKey = RedisConstants.FOLLOW_KEY + userID;
            //保存
            stringRedisTemplate.opsForSet().add(redisUserKey, followUserId.toString());
        }
        else
        {
            //不是关注，取消关注
            //删除数据库里的相关信息
            QueryWrapper<Follow> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("follow_user_id", followUserId).eq("user_id", userID);
            //删除
            boolean remove = this.remove(queryWrapper);
            if (!remove)
            {
                return Result.fail("取消关注失败");
            }
            //删除成功，移除redis相关数据
            //登录用户的key
            String redisUserKey = RedisConstants.FOLLOW_KEY + userID;
            //移除
            stringRedisTemplate.opsForSet().remove(redisUserKey, followUserId);
        }
        //返回
        return Result.ok();
    }

    @Override
    public Result isFollow(Long followUserId)
    {
        //获取当前用户的id
        Long userID = UserHolder.getUser().getId();
        //查数据库
        QueryWrapper<Follow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("follow_user_id", followUserId).eq("user_id", userID);
        long count = this.count(queryWrapper);
        //返回
        return Result.ok(count > 0);
    }

    @Override
    public Result followCommons(Long id)
    {
        //获取当前用户的id
        Long userID = UserHolder.getUser().getId();
        //获得redisKey
        //登录用户的key
        String redisUserKey = RedisConstants.FOLLOW_KEY + userID;
        //博主的key
        String redisBlogKey = RedisConstants.FOLLOW_KEY + id;
        //获得交集
        Set<String> intersect = stringRedisTemplate.opsForSet().intersect(redisUserKey, redisBlogKey);
        //判断是否为空
        if (intersect == null)
        {
            //返回空集合
            return Result.ok(Collections.emptyList());
        }
        if (intersect.size() == 0)
        {
            //返回空集合
            return Result.ok(Collections.emptyList());
        }
        //过滤获得id
        List<Long> ids = intersect.stream().map(Long::valueOf).collect(Collectors.toList());
        //查询
        List<User> users = userService.listByIds(ids);
        //转换
        List<UserDTO> collect = users.stream()
                .map(user -> (BeanUtil.copyProperties(user, UserDTO.class)))
                .collect(Collectors.toList());
        //返回
        return Result.ok(collect);
    }
}
