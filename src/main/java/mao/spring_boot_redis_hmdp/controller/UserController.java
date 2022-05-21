package mao.spring_boot_redis_hmdp.controller;


import cn.hutool.core.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import mao.spring_boot_redis_hmdp.dto.LoginFormDTO;
import mao.spring_boot_redis_hmdp.dto.Result;
import mao.spring_boot_redis_hmdp.dto.UserDTO;
import mao.spring_boot_redis_hmdp.entity.User;
import mao.spring_boot_redis_hmdp.entity.UserInfo;
import mao.spring_boot_redis_hmdp.service.IUserInfoService;
import mao.spring_boot_redis_hmdp.service.IUserService;
import mao.spring_boot_redis_hmdp.utils.UserHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@Slf4j
@RestController
@RequestMapping("/user")
public class UserController
{

    @Resource
    private IUserService userService;

    @Resource
    private IUserInfoService userInfoService;

    /**
     * 发送手机验证码
     */
    @PostMapping("code")
    public Result sendCode(@RequestParam("phone") String phone, HttpSession session)
    {
        return userService.sendCode(phone, session);
    }

    /**
     * 登录功能
     *
     * @param loginForm 登录参数，包含手机号、验证码；或者手机号、密码
     */
    @PostMapping("/login")
    public Result login(@RequestBody LoginFormDTO loginForm, HttpSession session)
    {
        return userService.login(loginForm, session);
    }

    /**
     * 登出功能
     *
     * @return 无
     */
    @PostMapping("/logout")
    public Result logout(HttpServletRequest request, HttpServletResponse response)
    {
        //实现登出功能
        return userService.logout(request,response);
    }

    @GetMapping("/me")
    public Result me()
    {
        return Result.ok(UserHolder.getUser());
    }

    @GetMapping("/info/{id}")
    public Result info(@PathVariable("id") Long userId)
    {
        // 查询详情
        UserInfo info = userInfoService.getById(userId);
        if (info == null)
        {
            // 没有详情，应该是第一次查看详情
            return Result.ok();
        }
        info.setCreateTime(null);
        info.setUpdateTime(null);
        // 返回
        return Result.ok(info);
    }

    /**
     * 根据查询用户信息
     *
     * @param userId 用户的id
     * @return Result
     */
    @GetMapping("/{id}")
    public Result queryUserById(@PathVariable("id") Long userId)
    {
        //查询用户信息
        User user = userService.getById(userId);
        if (user == null)
        {
            return Result.ok();
        }
        //转换
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        return Result.ok(userDTO);
    }

    /**
     * 实现用户签到功能
     *
     * @return Result
     */
    @PostMapping("/sign")
    public Result sign()
    {
        return userService.sign();
    }

    /**
     * 实现签到统计功能
     * 连续签到次数：从最后一次签到开始向前统计，直到遇到第一次未签到为止，计算总的签到次数
     *
     * @return Result
     */
    @GetMapping("/signCount")
    public Result signCount()
    {
        return userService.signCount();
    }
}

