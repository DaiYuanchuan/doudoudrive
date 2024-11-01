package com.doudoudrive.auth.config;

import com.alibaba.fastjson.JSON;
import com.doudoudrive.auth.client.UserInfoSearchFeignClient;
import com.doudoudrive.auth.manager.LoginManager;
import com.doudoudrive.auth.model.dto.MockToken;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.model.convert.DiskUserInfoConvert;
import com.doudoudrive.common.model.dto.model.LoginType;
import com.doudoudrive.common.model.dto.model.SysUserRoleModel;
import com.doudoudrive.common.model.dto.model.UserSimpleModel;
import com.doudoudrive.common.model.dto.model.auth.SysUserAuthModel;
import com.doudoudrive.common.model.dto.response.UserinfoSearchResponseDTO;
import com.doudoudrive.common.util.http.Result;
import com.doudoudrive.common.util.lang.CollectionUtil;
import com.doudoudrive.common.util.lang.SpringBeanFactoryUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * <p>配置Shiro继承AuthenticatingRealm类</p>
 * <p>2022-04-04 20:48</p>
 *
 * @author Dan
 **/
@Slf4j
@Component(value = "ShiroRealm")
public class ShiroRealm extends AuthorizingRealm {

    private UserInfoSearchFeignClient userInfoSearchFeignClient;
    private LoginManager loginManager = null;
    private DiskUserInfoConvert diskUserInfoConvert;

    @Autowired
    public void setUserInfoSearchFeignClient(UserInfoSearchFeignClient userInfoSearchFeignClient) {
        this.userInfoSearchFeignClient = userInfoSearchFeignClient;
    }

    @Autowired(required = false)
    public void setDiskUserInfoConvert(DiskUserInfoConvert diskUserInfoConvert) {
        this.diskUserInfoConvert = diskUserInfoConvert;
    }

    /**
     * 这是授权方法
     *
     * @param principalCollection 主集合
     * @return 授权信息
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        if (!SecurityUtils.getSubject().isAuthenticated()) {
            return authorizationInfo;
        }

        // 查看是否存在缓存中
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession(true);
        if (session.getAttribute(ConstantConfig.Cache.USER_ROLE_CACHE) != null) {
            return (SimpleAuthorizationInfo) session.getAttribute(ConstantConfig.Cache.USER_ROLE_CACHE);
        }

        // 手动注入服务
        if (loginManager == null) {
            loginManager = SpringBeanFactoryUtils.getBean(LoginManager.class);
        }

        // 获取当前登陆的用户信息
        Optional.ofNullable(loginManager.getUserInfoToSession()).ifPresent(userLoginInfo -> {
            // 向shiro中添加用户角色、权限相关信息
            for (SysUserRoleModel userRoleInfo : userLoginInfo.getUserInfo().getRoleInfo()) {
                // 添加角色
                authorizationInfo.addRole(userRoleInfo.getRoleCode());
                List<String> permissionsList = userRoleInfo.getAuthInfo().stream().map(SysUserAuthModel::getAuthCode).toList();
                if (CollectionUtil.isNotEmpty(permissionsList)) {
                    // 添加权限
                    authorizationInfo.addStringPermissions(permissionsList);
                }
            }
            // 在缓存中添加用户权限信息
            session.setAttribute(ConstantConfig.Cache.USER_ROLE_CACHE, authorizationInfo);
        });
        return authorizationInfo;
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

        if (diskUserInfoConvert == null) {
            diskUserInfoConvert = SpringBeanFactoryUtils.getBean(DiskUserInfoConvert.class);
        }

        // UsernamePasswordToken对象用来存放提交的登录信息
        UsernamePasswordToken upToken = (UsernamePasswordToken) authenticationToken;

        MockToken tk = (MockToken) authenticationToken;

        // 通过登陆的 用户名 、 用户邮箱 、 手机号 查找对应的用户信息
        Result<UserinfoSearchResponseDTO> usernameSearchResult = userInfoSearchFeignClient.usernameSearch(upToken.getUsername());
        if (Result.isNotSuccess(usernameSearchResult)) {
            // 抛出用户名不存在的异常
            throw new UnknownAccountException();
        }

        // 同时当前用户账号不可用
        if (!usernameSearchResult.getData().getAvailable()) {
            UserSimpleModel userSimpleModel = diskUserInfoConvert.usernameSearchResponseConvertUserSimpleModel(usernameSearchResult.getData());
            // 抛出禁用帐户异常
            throw new DisabledAccountException(JSON.toJSONString(userSimpleModel));
        }

        // 如果是免密登录直接返回
        if (tk.getType().equals(LoginType.NO_PASSWORD)) {
            return new SimpleAuthenticationInfo(upToken.getUsername(), upToken.getUsername(), getName());
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

    /**
     * 重写 supports 使之支持 MockToken
     *
     * @param token 验证的Token
     * @return 判断是否支持
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof UsernamePasswordToken;
    }
}
