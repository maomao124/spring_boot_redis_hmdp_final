package mao.spring_boot_redis_hmdp.utils;

import cn.hutool.core.lang.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * Project name(项目名称)：spring_boot_redis_hmdp_distributed_lock_realize_the_function_of_one_person_and_one_order
 * Package(包名): mao.spring_boot_redis_hmdp.utils
 * Class(类名): RedisLockImpl
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2022/5/17
 * Time(创建时间)： 10:53
 * Version(版本): 1.0
 * Description(描述)： 简单分布式锁，非单例
 */

@Slf4j
public class RedisLockImpl implements RedisLock
{
    /**
     * 锁的名称
     */
    private final String name;
    /**
     * StringRedisTemplate
     */
    private final StringRedisTemplate stringRedisTemplate;

    private static final String ID_PREFIX = UUID.randomUUID().toString(true) + "-";

    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT;

    static
    {
        //创建对象
        UNLOCK_SCRIPT = new DefaultRedisScript<>();
        //加载类路径下的资源
        UNLOCK_SCRIPT.setLocation(new ClassPathResource("unlock.lua"));
        //设置返回值的类型
        UNLOCK_SCRIPT.setResultType(Long.class);
    }

    /**
     * 锁前缀
     */
    private static final String KEY_PREFIX = "lock:";

    /**
     * 构造函数
     *
     * @param name                锁的名称
     * @param stringRedisTemplate StringRedisTemplate
     */
    public RedisLockImpl(String name, StringRedisTemplate stringRedisTemplate)
    {
        this.name = name;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean tryLock(long timeoutSec)
    {
        //获得线程标识
        long threadID = Thread.currentThread().getId();
        //锁key
        String lockKey = KEY_PREFIX + name;
        //获取锁
        Boolean result = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, String.valueOf(threadID),
                timeoutSec, TimeUnit.SECONDS);
        //返回
        return Boolean.TRUE.equals(result);
    }

    @Override
    public void unlock()
    {
        // 获取线程标识
        String threadID = ID_PREFIX + Thread.currentThread().getId();
        //锁key
        String lockKey = KEY_PREFIX + name;

        /*// 获取锁中的标识
        String id = stringRedisTemplate.opsForValue().get(lockKey);
        //判断锁是否是自己的，通过线程id来判断
        if (threadID.equals(id))
        {
            //释放
            stringRedisTemplate.delete(lockKey);
        }*/

        //执行lua脚本
        Long result = stringRedisTemplate.execute(UNLOCK_SCRIPT, Collections.singletonList(lockKey), threadID);
        if (result == null || result == 0)
        {
            //释放到了别人的锁
            log.debug("释放锁异常");
        }
    }
}
