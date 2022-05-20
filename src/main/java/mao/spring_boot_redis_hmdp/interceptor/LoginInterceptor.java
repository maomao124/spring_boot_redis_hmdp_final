package mao.spring_boot_redis_hmdp.interceptor;

import mao.spring_boot_redis_hmdp.utils.UserHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Project name(项目名称)：spring_boot_redis_hmdp_login_based_on_session
 * Package(包名): mao.spring_boot_redis_hmdp.interceptor
 * Class(类名): LoginInterceptor
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2022/5/12
 * Time(创建时间)： 21:17
 * Version(版本): 1.0
 * Description(描述)： 登录拦截器
 */

public class LoginInterceptor implements HandlerInterceptor
{

    /**
     * 拦截器校验用户
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @param handler  Object
     * @return boolean
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        // 判断是否拦截,查看ThreadLoad是否有用户
        if (UserHolder.getUser() == null)
        {
            //不存在，拦截，响应401
            response.setStatus(401);
            return false;
        }
        //放行
        return true;

    }
}
