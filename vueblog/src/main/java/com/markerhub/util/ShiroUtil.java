package com.markerhub.util;

import com.markerhub.shiro.AccountProfile;
import org.apache.shiro.SecurityUtils;

public class ShiroUtil {
    public static AccountProfile getProfile() {
        /*
        *
        * 在Shiro的认证流程中，SimpleAuthenticationInfo
        * 实例中的principal和credentials信息会被存储在当前会话的Subject中。
        你通过调用SecurityUtils.getSubject().getPrincipal()
        * 可以获取到Principal信息，也就是SimpleAuthenticationInfo中的第一个参数——在你的代码中，便是profile。
        这里的profile是你自定义的AccountProfile对象，你可以在doGetAuthenticationInfo方法中
        * 把任何你需要的用户信息填入到AccountProfile对象中，
        * 然后再包装成SimpleAuthenticationInfo对象返回，这样你就可以在任何地方通过SecurityUtils.getSubject().getPrincipal()
        * 来获取到这些信息。这就是你提到的定制化SimpleAuthenticationInfo对象的一种方式。
        注意，getPrincipal()返回的始终是你在认证过程中设置的Principal对象，
        * 除非你再次触发认证过程（如调用login()方法），
        * 然后返回一个新的SimpleAuthenticationInfo实例，
        * 否则getPrincipal()返回的对象不会变。*/
        return (AccountProfile) SecurityUtils.getSubject().getPrincipal();
    }
}
