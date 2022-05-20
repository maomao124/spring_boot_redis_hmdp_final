package mao.spring_boot_redis_hmdp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import mao.spring_boot_redis_hmdp.dto.LoginFormDTO;
import mao.spring_boot_redis_hmdp.dto.Result;
import mao.spring_boot_redis_hmdp.entity.User;

import javax.servlet.http.HttpSession;


public interface IUserService extends IService<User>
{
    /**
     * 发送短信验证码
     *
     * @param phone   手机号码
     * @param session HttpSession
     * @return Result
     */
    Result sendCode(String phone, HttpSession session);

    /**
     * 登录
     *
     * @param loginForm 登录信息，包含手机号和验证码（密码），一个实体类
     * @param session   HttpSession
     * @return Result
     */
    Result login(LoginFormDTO loginForm, HttpSession session);

    /**
     * 实现用户签到功能
     *
     * @return Result
     */
    Result sign();

    /**
     * 实现签到统计功能
     * 连续签到次数：从最后一次签到开始向前统计，直到遇到第一次未签到为止，计算总的签到次数
     *
     * @return Result
     */
    Result signCount();
}
