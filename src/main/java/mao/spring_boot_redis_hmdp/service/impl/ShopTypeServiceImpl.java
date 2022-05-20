package mao.spring_boot_redis_hmdp.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import mao.spring_boot_redis_hmdp.entity.ShopType;
import mao.spring_boot_redis_hmdp.mapper.ShopTypeMapper;
import mao.spring_boot_redis_hmdp.service.IShopTypeService;
import org.springframework.stereotype.Service;


@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService
{

}
