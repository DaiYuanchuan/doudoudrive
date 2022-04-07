package com.doudoudrive.auth.manager.impl;

import com.doudoudrive.auth.client.UserInfoSearchFeignClient;
import com.doudoudrive.auth.manager.LoginManager;
import com.doudoudrive.auth.manager.SysUserRoleManager;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.model.convert.DiskUserInfoConvert;
import com.doudoudrive.common.model.dto.model.DiskUserModel;
import com.doudoudrive.common.model.dto.response.UserLoginResponseDTO;
import com.doudoudrive.common.model.dto.response.UsernameSearchResponseDTO;
import com.doudoudrive.common.util.http.Result;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>登录服务通用业务处理层接口实现</p>
 * <p>2022-04-05 18:39</p>
 *
 * @author Dan
 **/
@Service("loginManager")
public class LoginManagerImpl implements LoginManager {

    /**
     * 用户搜索服务注入
     */
    private UserInfoSearchFeignClient userInfoSearchFeignClient;

    private DiskUserInfoConvert diskUserInfoConvert;

    private SysUserRoleManager sysUserRoleManager;

    @Autowired
    public void setUserInfoSearchFeignClient(UserInfoSearchFeignClient userInfoSearchFeignClient) {
        this.userInfoSearchFeignClient = userInfoSearchFeignClient;
    }

    @Autowired(required = false)
    public void setDiskUserInfoConvert(DiskUserInfoConvert diskUserInfoConvert) {
        this.diskUserInfoConvert = diskUserInfoConvert;
    }

    @Autowired
    public void setSysUserRoleManager(SysUserRoleManager sysUserRoleManager) {
        this.sysUserRoleManager = sysUserRoleManager;
    }

    /**
     * 从session中获取当前登录的用户信息
     * 不能从缓存中获取用户信息时，通过当前登录的用户名去搜素用户信息
     *
     * @return 返回用户登录模块响应数据DTO模型
     */
    @Override
    public UserLoginResponseDTO getUserInfoToSession() {
        try {
            if (SecurityUtils.getSubject().isAuthenticated()) {
                Subject subject = SecurityUtils.getSubject();
                Session session = subject.getSession(true);
                DiskUserModel userInfo = (DiskUserModel) session.getAttribute(ConstantConfig.Cache.USERINFO_CACHE);
                if (userInfo == null) {
                    String username = (String) SecurityUtils.getSubject().getPrincipal();
                    // 通过登陆的 用户名 查找对应的用户信息
                    Result<UsernameSearchResponseDTO> usernameSearchResult = userInfoSearchFeignClient.usernameSearch(username);
                    if (Result.isNotSuccess(usernameSearchResult)) {
                        return null;
                    }
                    userInfo = diskUserInfoConvert.usernameSearchResponseConvert(usernameSearchResult.getData());
                    // 获取当前登录用户所有角色、权限信息
                    userInfo.setRoleInfo(sysUserRoleManager.listSysUserRoleInfo(userInfo.getBusinessId()));
                    session.setAttribute(ConstantConfig.Cache.USERINFO_CACHE, userInfo);
                }

                // 构建用户登录模块响应数据DTO模型
                return UserLoginResponseDTO.builder()
                        .userInfo(userInfo)
                        .token(session.getId().toString())
                        .build();
            }
        } catch (UnknownSessionException ignored) {
        }
        return null;
    }
}
