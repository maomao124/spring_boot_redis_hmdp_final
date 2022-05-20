package mao.spring_boot_redis_hmdp.controller;


import mao.spring_boot_redis_hmdp.dto.Result;
import mao.spring_boot_redis_hmdp.entity.ShopType;
import mao.spring_boot_redis_hmdp.service.IShopTypeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/shop-type")
public class ShopTypeController
{
    @Resource
    private IShopTypeService typeService;

    @GetMapping("list")
    public Result queryTypeList()
    {
        List<ShopType> typeList = typeService
                .query().orderByAsc("sort").list();
        return Result.ok(typeList);
    }
}
