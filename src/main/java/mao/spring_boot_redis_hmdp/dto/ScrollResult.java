package mao.spring_boot_redis_hmdp.dto;

import lombok.Data;

import java.util.List;

@Data
public class ScrollResult
{
    /**
     * list集合，用于放分页数据
     */
    private List<?> list;
    /**
     * 最小的时间戳
     */
    private Long minTime;
    /**
     * 偏移量
     */
    private Integer offset;
}
