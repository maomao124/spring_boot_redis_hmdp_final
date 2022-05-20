package mao.spring_boot_redis_hmdp.controller;


import mao.spring_boot_redis_hmdp.dto.Result;
import mao.spring_boot_redis_hmdp.service.IFollowService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@RestController
@RequestMapping("/follow")
public class FollowController
{

    @Resource
    private IFollowService followService;

    /**
     * 关注或者取消关注，这取决于isFollow的值
     *
     * @param followUserId 被关注的用户的id
     * @param isFollow     如果是关注，则为true，否则为false
     * @return Result
     */
    @PutMapping("/{id}/{isFollow}")
    public Result follow(@PathVariable("id") Long followUserId, @PathVariable("isFollow") Boolean isFollow)
    {
        return followService.follow(followUserId, isFollow);
    }

    /**
     * 判断当前用户是否关注了 用户id为followUserId的人
     *
     * @param followUserId 被关注的用户的id
     * @return Result
     */
    @PutMapping("/or/not/{id}")
    public Result isFollow(@PathVariable("id") Long followUserId)
    {
        return followService.isFollow(followUserId);
    }

    /**
     * 获取共同关注的人
     *
     * @param id 当前博主的id
     * @return Result
     */
    @GetMapping("/common/{id}")
    public Result followCommons(@PathVariable("id") Long id)
    {
        return followService.followCommons(id);
    }

}
