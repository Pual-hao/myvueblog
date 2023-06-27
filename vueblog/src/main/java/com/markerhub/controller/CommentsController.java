package com.markerhub.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.markerhub.common.lang.Result;
import com.markerhub.entity.Blog;
import com.markerhub.entity.Comments;
import com.markerhub.service.CommentsService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author anonymous
 * @since 2023-06-27
 */
@RestController
@RequestMapping("/comments")
public class CommentsController {

    @Autowired
    CommentsService commentsService;

    //
    @GetMapping("/{blogId}")
    public Result getComment(@PathVariable(name = "blogId") Long id,
                          @RequestParam(defaultValue = "1") Integer currentPage){
        Map<String,Object> map = new HashMap<>();
        map.put("blog_id", id);
        // 默认调取每页为8个评论
        Page page = new Page(currentPage, 8);

        //按创建时间降序排列,取出第currentPage页的5个数据
        IPage pageData = commentsService.page(page, new QueryWrapper<Comments>().allEq(map).orderByDesc("created"));

        return Result.succ(pageData);
    }


    @PostMapping("/{blogId}")
    public Result postComment(@PathVariable(name = "blogId") Long id,
                              @Validated @RequestBody Comments comments){
        comments.setBlogId(id);
        comments.setCreated(LocalDateTime.now());
        commentsService.saveOrUpdate(comments);
        return Result.succ(null);
    }

    // 删除评论
    @RequiresAuthentication
    @PostMapping("/delete/{id}")
    public Result delete(@PathVariable(name = "id") Long id){
        boolean b = commentsService.removeById(id);

        if(b){
            //删除成功
            return Result.succ("评论删除成功");
        }
        else{
            return Result.fail("评论删除失败");
        }

    }
}
