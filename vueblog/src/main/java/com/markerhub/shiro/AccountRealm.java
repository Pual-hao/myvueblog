package com.markerhub.shiro;

import cn.hutool.core.bean.BeanUtil;
import com.markerhub.entity.User;
import com.markerhub.service.UserService;
import com.markerhub.util.JwtUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccountRealm extends AuthorizingRealm {
    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserService userService;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    //获取授权信息
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }

    // 分析源码之后，发现在Subject.login里会调用这个函数，并且会将SimpleAuthenticationInfo这个返回值
    // 作为Security的当前值，在调用下一次executeLogin更换里面的值之前，
    // 使用SecurityUtils.getSubject().getPrincipal()函数返回的都是
    // 当前返回的SimpleAuthenticationInfo里的profile
    //获取验证信息
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        JwtToken jwtToken= (JwtToken) token;
        //userId一般存放在Claims的Subject中
        String userId = jwtUtils.getClaimByToken((String) jwtToken.getPrincipal()).getSubject();
        User user = userService.getById(Long.valueOf(userId));

        if(user ==null){
            throw new UnknownAccountException("账户不存在");
        }
        //账户被锁定
        if(user.getStatus() == -1){
            throw new LockedAccountException("账户已被锁定");
        }

        AccountProfile profile = new AccountProfile();
        BeanUtil.copyProperties(user,profile);


        System.out.println();

        return new SimpleAuthenticationInfo(profile,jwtToken.getCredentials(),this.getName());
    }
}
