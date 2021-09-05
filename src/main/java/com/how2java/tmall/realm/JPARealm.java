package com.how2java.tmall.realm;

import com.how2java.tmall.pojo.User;
import com.how2java.tmall.service.UserService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

public class JPARealm extends AuthorizingRealm {
    @Autowired
    UserService userService;

    @Override
    //授权，该方法需要的参数是PrincipalCollection对象，
    // 这个对象表示经过认证后的登录主体，这个方法作用就是要给这个登录的主体授权，返回一个授权后的主体simpleAuthorizationInfo
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //通过用户名从数据库获取权限字符串
        SimpleAuthorizationInfo s = new SimpleAuthorizationInfo();

        return s;
    }

    @Override
    //认证，该方法需要的参数是AuthenticationToken对象，
    // AuthenticationToken 用于收集前端提交的身份（如用户名）及凭据（如密码），
    // 通过该参数传入数据与后端用户数据（用户数据库等）进行密码比对，最终判断用户登录成功与否
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String userName = authenticationToken.getPrincipal().toString();
        User user = userService.getByName(userName);
        String passwordInDB = user.getPassWord();
        String salt = user.getSalt();
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(userName, passwordInDB, ByteSource.Util.bytes(salt),
                getName());
        return authenticationInfo;
    }
}
