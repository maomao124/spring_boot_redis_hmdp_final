package mao.spring_boot_redis_hmdp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Project name(项目名称)：spring_boot_redis_hmdp_use_logical_expiration_solve_cache_breakdown_problem
 * Package(包名): mao.spring_boot_redis_hmdp.dto
 * Class(类名): RedisData
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2022/5/14
 * Time(创建时间)： 19:58
 * Version(版本): 1.0
 * Description(描述)： 无
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedisData
{
    private LocalDateTime expireTime;
    private Object data;
}
