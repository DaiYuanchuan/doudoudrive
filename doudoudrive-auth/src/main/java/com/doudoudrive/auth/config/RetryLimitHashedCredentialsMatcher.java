package com.doudoudrive.auth.config;

import com.doudoudrive.auth.model.dto.MockToken;
import com.doudoudrive.common.model.dto.model.LoginType;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;

/**
 * <p>重写shiro密码校验</p>
 * <p>2022-04-04 21:06</p>
 *
 * @author Dan
 **/
public class RetryLimitHashedCredentialsMatcher extends HashedCredentialsMatcher {

    @Override
    public boolean doCredentialsMatch(AuthenticationToken authToken, AuthenticationInfo info) {
        MockToken tk = (MockToken) authToken;
        // 如果是免密登录直接返回true
        if (tk.getType().equals(LoginType.NO_PASSWORD)) {
            return true;
        }
        // 不是免密登录，调用父类的方法
        return super.doCredentialsMatch(tk, info);
    }

}