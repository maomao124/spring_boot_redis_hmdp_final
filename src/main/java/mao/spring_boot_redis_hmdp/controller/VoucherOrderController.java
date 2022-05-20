package mao.spring_boot_redis_hmdp.controller;


import mao.spring_boot_redis_hmdp.dto.Result;
import mao.spring_boot_redis_hmdp.service.IVoucherOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/voucher-order")
public class VoucherOrderController
{
    @Autowired
    IVoucherOrderService voucherOrderService;

    @PostMapping("seckill/{id}")
    public Result seckillVoucher(@PathVariable("id") Long voucherId)
    {
        return voucherOrderService.seckillVoucher(voucherId);
    }
}
