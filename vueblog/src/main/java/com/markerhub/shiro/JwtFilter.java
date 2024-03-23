package com.markerhub.shiro;

import cn.hutool.json.JSONUtil;
import com.markerhub.common.lang.Result;
import com.markerhub.util.JwtUtils;
import io.jsonwebtoken.Claims;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
public class JwtFilter extends AuthenticatingFilter {

    @Autowired
    JwtUtils jwtUtils;

    //前端与后端通过Request和Response通信
    //token记录在Request中
    //shiro登录处理时会用到 createToken作用--获取token
    @Override
    protected AuthenticationToken createToken(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        //提供token以供shiro登陆处理
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String jwt = request.getHeader("Authorization");
        if (StringUtils.isEmpty(jwt)) {
            return null;
        }
        return new JwtToken(jwt);
    }

    /*在Apache Shiro的过滤器中，onAccessDenied方法应对请求被拒绝访问的情况。
    当这个方法返回true时，表示当前请求被允许访问，请求将继续被处理，
    传递到下一个过滤器或者最终的请求目标。
    它实质上告诉Shiro框架：尽管这个请求在某些条件判断下被认为被拒绝访问
    （例如没有身份凭证或者所需的权限等），但在onAccessDenied方法内部，已经处理了这个问题，
    所以请求应该继续进行，而不是被拒绝。
    在你的代码中，如果请求没有附带JWT令牌（视为游客身份），
    或者附带的JWT是有效的并且对应的用户可以成功登录，那么onAccessDenied就会返回true,
    请求就会继续被处理。*/
    //执行过滤
    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String jwt = request.getHeader("Authorization");
        //如果jwt是空的,直接略过Filter处理,类似于游客登陆
        if (StringUtils.isEmpty(jwt)) {
            return true;
        } else {

            //校验jwt
            Claims claim = jwtUtils.getClaimByToken(jwt);
            if (claim == null || jwtUtils.isTokenExpired(claim.getExpiration())) {
                throw new ExpiredCredentialsException("token已失效,请重新登录");
            }

            //执行Subject登录  如果登录成功，框架会自动将Subject.isAuthenticated()设置为true
            /*
            *
            * 这个login()方法会触发Shiro的认证流程。
            * 如果这个流程顺利完成，那么Subject.isAuthenticated()就会被设置为true，
            * 否则login()方法会抛出各种类型的AuthenticationException。
            注意，让整个认证流程能够正常运行还依赖一个自定义的Realm，
            * 这个Realm需要正确地处理JwtToken并进行认证。
            * 所以，你还需要在你的Shiro配置文件(Configuration file)中，
            * 将这个自定义的Realm注册进去。
            当你的代码正确地配置了以上各项，
            * 那么@RequiresAuthentication就会如你所期望地工作。*/
            /* 由于前端cookie中没有存储Subject会话信息，所以每一次访问受限资源，即
               每一次使用excuteLogin函数都会创建一个新的Subject会话信息，在使用@GetMapping("/logout")
               接口的时候，由于有@RequiresAuthentication注解，所以先会创建一个Subject会话，然后终止掉这个会话
               所以包括修改博客的时候subject之类的通过@RequiresAuthentication注解创建的会话信息会一直存在Redis中

               */
            return executeLogin(servletRequest, servletResponse);
        }

    }

    // 登录失败 可能有很多原因,token验证失败,权限错误等等
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        Throwable throwable = e.getCause() == null ? e : e.getCause();
        Result result = Result.fail(throwable.getMessage());
        String json = JSONUtil.toJsonStr(result);

        // 将错误信息以json的形式返回给Response,以供前端使用
        try {
            httpServletResponse.getWriter().print(json);
        } catch (IOException ex) {

        }

        return false;
    }

    /**
     * 对跨域提供支持
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        // 跨域时会首先发送一个OPTIONS请求,这里我们给OPTIONS请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(org.springframework.http.HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }
}
