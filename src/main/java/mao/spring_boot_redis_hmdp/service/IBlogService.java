package mao.spring_boot_redis_hmdp.service;


import com.baomidou.mybatisplus.extension.service.IService;
import mao.spring_boot_redis_hmdp.dto.Result;
import mao.spring_boot_redis_hmdp.entity.Blog;


public interface IBlogService extends IService<Blog>
{
    /**
     * 查询热门的探店笔记
     *
     * @param current 当前页
     * @return Result
     */
    Result queryHotBlog(Integer current);

    /**
     * 根据id进行查询
     *
     * @param id id
     * @return Result
     */
    Result queryBlogById(String id);

    /**
     * 点赞功能
     *
     * @param id id
     * @return result
     */
    Result likeBlog(Long id);

    /**
     * 点赞排行榜
     *
     * @param id id
     * @return Result
     */
    Result queryBlogLikes(String id);

    /**
     * 保存（发布）博客信息
     *
     * @param blog Blog
     * @return Result
     */
    Result saveBlog(Blog blog);

    /**
     * 从收件箱里取关注的人发的信息
     *
     * @param max    时间戳，第一页为当前时间，第n页为第n-1页最后一条数据的时间戳
     * @param offset 偏移量，第一页为0，不是第一页，取决于上一页最后一个时间戳的条数
     * @return Result
     */
    Result queryBlogOfFollow(Long max, Integer offset);
}
