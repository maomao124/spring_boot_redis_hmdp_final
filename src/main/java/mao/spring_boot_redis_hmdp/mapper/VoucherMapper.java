package mao.spring_boot_redis_hmdp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import mao.spring_boot_redis_hmdp.entity.Voucher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VoucherMapper extends BaseMapper<Voucher>
{

    List<Voucher> queryVoucherOfShop(@Param("shopId") Long shopId);
}
