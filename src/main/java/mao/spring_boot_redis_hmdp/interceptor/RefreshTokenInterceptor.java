package mao.spring_boot_redis_hmdp.interceptor;

import cn.hutool.core.bean.BeanUtil;
import mao.spring_boot_redis_hmdp.dto.UserDTO;
import mao.spring_boot_redis_hmdp.utils.RedisConstants;
import mao.spring_boot_redis_hmdp.utils.UserHolder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Project name(项目名称)：spring_boot_redis_hmdp_login_based_on_redis
 * Package(包名): mao.spring_boot_redis_hmdp.interceptor
 * Class(类名): RefreshTokenInterceptor
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2022/5/13
 * Time(创建时间)： 21:20
 * Version(版本): 1.0
 * Description(描述)： 刷新token的拦截器，毕竟不需要登录的请求也需要刷新
 */

public class RefreshTokenInterceptor implements HandlerInterceptor
{
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 构造函数
     *
     * @param stringRedisTemplate StringRedisTemplate
     */
    public RefreshTokenInterceptor(StringRedisTemplate stringRedisTemplate)
    {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 拦截器刷新token的过期时间
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @param handler  Object
     * @return boolean
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        //从请求头里获取token
        String token = request.getHeader("authorization");
        //判断token是否存在
        if (token == null || token.equals(""))
        {
            //不存在，拦截
            return false;
        }
        //token存在，根据token获取redisKey
        String redisKey = RedisConstants.LOGIN_USER_KEY + token;
        Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(redisKey);
        //
        //判断用户是否存在，userMap为空
        if (userMap.isEmpty())
        {
            //不存在，拦截，响应401
            response.setStatus(401);
            return false;
        }
        //存在
        //转实体类
        UserDTO userDTO = BeanUtil.fillBeanWithMap(userMap, new UserDTO(), false);
        //保存到ThreadLoad
        UserHolder.saveUser(BeanUtil.copyProperties(userDTO, UserDTO.class));
        //刷新过期时间，类似于session机制
        stringRedisTemplate.expire(redisKey, RedisConstants.LOGIN_USER_TTL, TimeUnit.MINUTES);
        //放行
        return true;
    }

    /**
     * 渲染之后，返回用户之前。 用户执行完毕后，销毁对应的用户信息
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @param handler  Object
     * @param ex       Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception
    {
        UserHolder.removeUser();
    }
}
