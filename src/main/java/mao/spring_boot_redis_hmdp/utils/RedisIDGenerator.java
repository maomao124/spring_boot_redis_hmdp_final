package mao.spring_boot_redis_hmdp.utils;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Project name(项目名称)：spring_boot_redis_hmdp_global_id_generator
 * Package(包名): mao.spring_boot_redis_hmdp.utils
 * Class(类名): RedisIDGenerator
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2022/5/15
 * Time(创建时间)： 12:24
 * Version(版本): 1.0
 * Description(描述)： id生成器
 */

@Component
public class RedisIDGenerator
{
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 2022年1月1日的时间
     */
    private static final long BEGIN_TIMESTAMP = 1640995200L;

    /**
     * 序列号位数
     */
    private static int COUNT_BITS = 32;

    /**
     * 获取一个id
     *
     * @param prefix 前缀
     * @return id
     */
    public Long nextID(String prefix)
    {
        //获取当前时间
        LocalDateTime now = LocalDateTime.now();
        //转换成秒数
        long nowSecond = now.toEpochSecond(ZoneOffset.UTC);
        //获得时间差
        long time = nowSecond - BEGIN_TIMESTAMP;
        //格式化成字符串
        String format = now.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
        //将key下存储为字符串值的整数值加一
        Long count = stringRedisTemplate.opsForValue().increment("id:" + prefix + ":" + format);
        return time << COUNT_BITS | count;
    }

}
