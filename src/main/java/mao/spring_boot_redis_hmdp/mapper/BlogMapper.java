package mao.spring_boot_redis_hmdp.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import mao.spring_boot_redis_hmdp.entity.Blog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BlogMapper extends BaseMapper<Blog>
{

}
