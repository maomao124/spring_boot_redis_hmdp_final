package mao.spring_boot_redis_hmdp.service;


import com.baomidou.mybatisplus.extension.service.IService;
import mao.spring_boot_redis_hmdp.dto.Result;
import mao.spring_boot_redis_hmdp.entity.VoucherOrder;


public interface IVoucherOrderService extends IService<VoucherOrder>
{

    /**
     * 秒杀优惠券
     *
     * @param voucherId 优惠券id
     * @return Result
     */
    Result seckillVoucher(Long voucherId);
}
