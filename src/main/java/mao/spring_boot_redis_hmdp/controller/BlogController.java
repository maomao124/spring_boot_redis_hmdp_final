package mao.spring_boot_redis_hmdp.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import mao.spring_boot_redis_hmdp.dto.Result;
import mao.spring_boot_redis_hmdp.dto.UserDTO;
import mao.spring_boot_redis_hmdp.entity.Blog;
import mao.spring_boot_redis_hmdp.service.IBlogService;
import mao.spring_boot_redis_hmdp.utils.SystemConstants;
import mao.spring_boot_redis_hmdp.utils.UserHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/blog")
public class BlogController
{

    @Resource
    private IBlogService blogService;


    /**
     * 保存（发布）博客信息
     *
     * @param blog Blog
     * @return Result
     */
    @PostMapping
    public Result saveBlog(@RequestBody Blog blog)
    {
        return blogService.saveBlog(blog);
    }

    @PutMapping("/like/{id}")
    public Result likeBlog(@PathVariable("id") Long id)
    {
        return blogService.likeBlog(id);
    }

    @GetMapping("/of/me")
    public Result queryMyBlog(@RequestParam(value = "current", defaultValue = "1") Integer current)
    {
        // 获取登录用户
        UserDTO user = UserHolder.getUser();
        // 根据用户查询
        Page<Blog> page = blogService.query()
                .eq("user_id", user.getId()).page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
        // 获取当前页数据
        List<Blog> records = page.getRecords();
        return Result.ok(records);
    }

    @GetMapping("/hot")
    public Result queryHotBlog(@RequestParam(value = "current", defaultValue = "1") Integer current)
    {
        return blogService.queryHotBlog(current);
    }

    @GetMapping("/{id}")
    public Result queryBlogById(@PathVariable("id") String id)
    {
        return blogService.queryBlogById(id);
    }

    @GetMapping("/likes/{id}")
    public Result queryBlogLikes(@PathVariable("id") String id)
    {
        return blogService.queryBlogLikes(id);
    }

    /**
     * 查询用户的笔记信息
     *
     * @param current 当前页，如果不指定，则为第一页
     * @param id      博主的id
     * @return Result
     */
    @GetMapping("/of/user")
    public Result queryBlogByUserId(@RequestParam(value = "current", defaultValue = "1") Integer current,
                                    @RequestParam("id") Long id)
    {
        //根据用户查询
        Page<Blog> page = blogService.query().
                eq("user_id", id).
                page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
        //获取当前页数据
        List<Blog> records = page.getRecords();
        //返回
        return Result.ok(records);
    }

    /**
     * 从收件箱里取关注的人发的信息
     *
     * @param max    时间戳，第一页为当前时间，第n页为第n-1页最后一条数据的时间戳
     * @param offset 偏移量，第一页为0，不是第一页，取决于上一页最后一个时间戳的条数
     * @return Result
     */
    @GetMapping("/of/follow")
    public Result queryBlogOfFollow(@RequestParam("lastId") Long max,
                                    @RequestParam(value = "offset", defaultValue = "0") Integer offset)
    {
        return blogService.queryBlogOfFollow(max, offset);
    }

}
