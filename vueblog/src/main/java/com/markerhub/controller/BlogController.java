package com.markerhub.controller;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.markerhub.common.lang.Result;
import com.markerhub.entity.Blog;
import com.markerhub.service.BlogService;
import com.markerhub.util.ShiroUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author anonymous
 * @since 2023-06-18
 */
@RestController
public class BlogController {


    @Autowired
    BlogService blogService;

    @GetMapping("/blogs")
    public Result list(@RequestParam(defaultValue = "1") Integer currentPage) {

        Page page = new Page(currentPage, 5);

        //按创建时间降序排列,取出第currentPage页的5个数据
        IPage pageData = blogService.page(page, new QueryWrapper<Blog>().orderByDesc("created"));
        return Result.succ(pageData);
    }

    // 访问某文章
    @GetMapping("/blog/{id}")
    public Result detail(@PathVariable(name = "id") Long id) {
        Blog blog = blogService.getById(id);
        Assert.notNull(blog, "该博客已被删除");

        return Result.succ(blog);
    }

    // 删除文章
    @RequiresAuthentication
    @PostMapping("/blog/{id}")
    public Result delete(@PathVariable(name = "id") Long id){
        boolean b = blogService.removeById(id);

        if(b){
            //删除成功
            return Result.succ("文章删除成功");
        }
        else{
            return Result.fail("文章删除失败");
        }

    }


    @RequiresAuthentication
    @PostMapping("/blog/edit")
    public Result edit(@Validated @RequestBody Blog blog) {

        //一个空对象用于赋值
        Blog temp = null;
        //编辑文章
        if (blog.getId() != null) {
            temp = blogService.getById(blog.getId());
            //只能编辑自己的文章
            Assert.isTrue(temp.getUserId().equals(ShiroUtil.getProfile().getId()),"没有权限编辑");

        } else { // 添加文章

            temp = new Blog();
            temp.setUserId(ShiroUtil.getProfile().getId());
            temp.setCreated(LocalDateTime.now());
            temp.setStatus(0);

        }

        //将blog的值赋给temp 忽略 id userid created status 引用于hutool
        BeanUtil.copyProperties(blog,temp,"id","userId","created","status");
        blogService.saveOrUpdate(temp);

        return Result.succ(null);
    }
}
