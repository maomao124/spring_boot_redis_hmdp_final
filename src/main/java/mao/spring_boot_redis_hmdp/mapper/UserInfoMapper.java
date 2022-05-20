package mao.spring_boot_redis_hmdp.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import mao.spring_boot_redis_hmdp.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo>
{

}
