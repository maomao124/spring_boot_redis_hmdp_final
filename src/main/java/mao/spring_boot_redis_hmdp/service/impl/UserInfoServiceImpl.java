package mao.spring_boot_redis_hmdp.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import mao.spring_boot_redis_hmdp.entity.UserInfo;
import mao.spring_boot_redis_hmdp.mapper.UserInfoMapper;
import mao.spring_boot_redis_hmdp.service.IUserInfoService;
import org.springframework.stereotype.Service;


@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService
{

}
