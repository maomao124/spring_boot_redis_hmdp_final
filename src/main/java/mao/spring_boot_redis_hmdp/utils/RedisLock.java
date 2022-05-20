package mao.spring_boot_redis_hmdp.utils;

/**
 * Project name(项目名称)：spring_boot_redis_hmdp_distributed_lock_realize_the_function_of_one_person_and_one_order
 * Package(包名): mao.spring_boot_redis_hmdp.utils
 * Interface(接口名): RedisLock
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2022/5/17
 * Time(创建时间)： 10:51
 * Version(版本): 1.0
 * Description(描述)： 无
 */

public interface RedisLock
{
    /**
     * 尝试获取锁
     *
     * @param timeoutSec 超时时间
     * @return boolean，成功返回true，否则返回false
     */
    boolean tryLock(long timeoutSec);

    /**
     * 释放锁
     */
    void unlock();
}
