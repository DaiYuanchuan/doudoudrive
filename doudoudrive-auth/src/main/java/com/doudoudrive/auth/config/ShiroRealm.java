package com.doudoudrive.auth.config;

import com.alibaba.fastjson.JSON;
import com.doudoudrive.auth.client.UserInfoSearchFeignClient;
import com.doudoudrive.auth.model.dto.MockToken;
import com.doudoudrive.common.model.dto.model.LoginType;
import com.doudoudrive.common.model.dto.response.UsernameSearchResponseDTO;
import com.doudoudrive.common.util.http.Result;
import com.doudoudrive.common.util.lang.SpringBeanFactoryUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>配置Shiro继承AuthenticatingRealm类</p>
 * <p>2022-04-04 20:48</p>
 *
 * @author Dan
 **/
@Slf4j
@Component(value = "ShiroRealm")
public class ShiroRealm extends AuthorizingRealm {

    /**
     * 用户搜索服务注入
     */
    private UserInfoSearchFeignClient userInfoSearchFeignClient;

    @Autowired
    public void setUserInfoSearchFeignClient(UserInfoSearchFeignClient userInfoSearchFeignClient) {
        this.userInfoSearchFeignClient = userInfoSearchFeignClient;
    }

    /**
     * 这是授权方法
     *
     * @param principalCollection 主集合
     * @return 授权信息
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }

    /**
     * 这是认证方法
     *
     * @param authenticationToken 用来认证的token
     * @return 身份信息
     * @throws AuthenticationException 抛出 身份验证异常
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        // 手动注入服务
        if (userInfoSearchFeignClient == null) {
            userInfoSearchFeignClient = SpringBeanFactoryUtils.getBean(UserInfoSearchFeignClient.class);
        }

        // UsernamePasswordToken对象用来存放提交的登录信息
        UsernamePasswordToken upToken = (UsernamePasswordToken) authenticationToken;

        MockToken tk = (MockToken) authenticationToken;
        // 如果是免密登录直接返回
        if (tk.getType().equals(LoginType.NO_PASSWORD)) {
            return new SimpleAuthenticationInfo(upToken.getUsername(), upToken.getUsername(), getName());
        }

        // 通过登陆的 用户名 、 用户邮箱 、 手机号 查找对应的用户信息
        Result<UsernameSearchResponseDTO> usernameSearchResult = userInfoSearchFeignClient.usernameSearch(upToken.getUsername());
        if (Result.isNotSuccess(usernameSearchResult)) {
            // 抛出用户名不存在的异常
            throw new UnknownAccountException();
        }

        // 同时当前用户账号不可用
        if (!usernameSearchResult.getData().getAvailable()) {
            // 抛出禁用帐户异常
            throw new DisabledAccountException(JSON.toJSONString(usernameSearchResult.getData()));
        }

        // 这里的盐值可以自定义
        ByteSource credentialsSalt = ByteSource.Util.bytes(usernameSearchResult.getData().getUserSalt());
        // 将此用户存放到登录认证info中
        return new SimpleAuthenticationInfo(
                // 用户名
                usernameSearchResult.getData().getUserName(),
                // 密码
                usernameSearchResult.getData().getUserPwd(),
                // 盐值 = 随机32数
                credentialsSalt,
                getName()
        );
    }
}
