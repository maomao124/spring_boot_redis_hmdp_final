package mao.spring_boot_redis_hmdp.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Project name(项目名称)：spring_boot_redis_hmdp_distributed_lock_based_on_redisson
 * Package(包名): mao.spring_boot_redis_hmdp.config
 * Class(类名): RedissonConfig
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2022/5/17
 * Time(创建时间)： 13:40
 * Version(版本): 1.0
 * Description(描述)： Redisson的配置
 */

@Configuration
public class RedissonConfig
{
    /**
     * Redisson配置
     *
     * @return RedissonClient
     */
    @Bean
    public RedissonClient redissonClient()
    {
        //配置类
        Config config = new Config();
        //添加redis地址，用config.useClusterServers()添加集群地址
        config.useSingleServer().setAddress("redis://127.0.0.1:6379").setPassword("123456");
        //创建客户端
        return Redisson.create(config);
    }
}
