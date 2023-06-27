package com.markerhub.controller;

import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.markerhub.common.dto.LoginDto;
import com.markerhub.common.lang.Result;
import com.markerhub.entity.User;
import com.markerhub.service.UserService;
import com.markerhub.util.JwtUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
public class AccountController {

    @Autowired
    UserService userService;

    @Autowired
    JwtUtils jwtUtils;

    /*通过@requestBody可以将请求体中的JSON字符串绑定到相应的bean上，当然，也可以将其分别绑定到对应的字符串上。
　　　　例如说以下情况：
　　　　$.ajax({
　　　　　　　　url:"/login",
　　　　　　　　type:"POST",
　　　　　　　　data:'{"userName":"admin","pwd","admin123"}',
　　　　　　　　content-type:"application/json charset=utf-8",
　　　　　　　　success:function(data){
　　　　　　　　　　alert("request success ! ");
　　　　　　　　}
　　　　});

　　　　@requestMapping("/login")
　　　　public void login(@requestBody String userName,@requestBody String pwd){
　　　　　　System.out.println(userName+" ："+pwd);
　　　　}
　　　　这种情况是将JSON字符串中的两个变量的值分别赋予了两个字符串，但是呢,假如我有一个User类，拥有如下字段：
　　　　　　String userName;
　　　　　　String pwd;
　　　　那么上述参数可以改为以下形式：@requestBody User user 这种形式会将JSON字符串中的值赋予user中对应的属性上
　　　　需要注意的是，JSON字符串中的key必须对应user中的属性名，否则是请求不过去的。*/
    @PostMapping("/login")
    public Result login(@Validated @RequestBody LoginDto loginDto, HttpServletResponse response){

        //根据前端传入的用户名在数据库中查找username对应的User数据
        User user = userService.getOne(new QueryWrapper<User>().eq("username", loginDto.getUsername()));
        /*经由全局异常处理之后,虽然异常处理也会给Response返回一个Result数据,但Response会标记为Error
        * 所以在后面axios拦截的时候需要用error来处理这种情况*/
        Assert.notNull(user,"用户不存在");

        //数据库里保存的密码是md5加密过的
        if(!user.getPassword().equals(SecureUtil.md5(loginDto.getPassword()))){
            return Result.fail("密码不正确");
        }
        String jwt = jwtUtils.generateToken(user.getId());

        //将token 放在我们的header里面
        response.setHeader("Authorization",jwt);
        response.setHeader("Access-control-Expose-Headers","Authorization");


        return Result.succ(MapUtil.builder()
                .put("id",user.getId())
                .put("username",user.getUsername())
                .put("avatar",user.getAvatar())
                .put("email",user.getEmail())
                .map()
        );
    }

    @RequiresAuthentication
    @GetMapping("/logout")
    public Result logout(){
        SecurityUtils.getSubject().logout();
        return Result.succ(null);
    }
}
