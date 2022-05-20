package mao.spring_boot_redis_hmdp.service;


import com.baomidou.mybatisplus.extension.service.IService;
import mao.spring_boot_redis_hmdp.dto.Result;
import mao.spring_boot_redis_hmdp.entity.Follow;


public interface IFollowService extends IService<Follow>
{

    /**
     * 关注或者取消关注，这取决于isFollow的值
     *
     * @param followUserId 被关注的用户的id
     * @param isFollow     如果是关注，则为true，否则为false
     * @return Result
     */
    Result follow(Long followUserId, Boolean isFollow);

    /**
     * 判断当前用户是否关注了 用户id为followUserId的人
     *
     * @param followUserId 被关注的用户的id
     * @return Result
     */
    Result isFollow(Long followUserId);

    /**
     * 获取共同关注的人
     *
     * @param id 博主的id
     * @return Result
     */
    Result followCommons(Long id);
}
