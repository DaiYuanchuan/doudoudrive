package com.doudoudrive.auth.manager.impl;

import com.doudoudrive.auth.client.UserInfoSearchFeignClient;
import com.doudoudrive.auth.manager.LoginManager;
import com.doudoudrive.auth.manager.SysUserRoleManager;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.global.BusinessException;
import com.doudoudrive.common.global.BusinessExceptionUtil;
import com.doudoudrive.common.global.StatusCodeEnum;
import com.doudoudrive.common.model.convert.DiskUserInfoConvert;
import com.doudoudrive.common.model.dto.model.DiskUserModel;
import com.doudoudrive.common.model.dto.model.UserConfidentialInfo;
import com.doudoudrive.common.model.dto.response.UserLoginResponseDTO;
import com.doudoudrive.common.model.dto.response.UsernameSearchResponseDTO;
import com.doudoudrive.common.util.http.Result;
import com.doudoudrive.commonservice.service.DiskUserAttrService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>登录鉴权服务通用业务处理层接口实现</p>
 * <p>2022-04-05 18:39</p>
 *
 * @author Dan
 **/
@Service("loginManager")
public class LoginManagerImpl implements LoginManager {

    private DiskUserInfoConvert diskUserInfoConvert;

    private SysUserRoleManager sysUserRoleManager;

    private UserInfoSearchFeignClient userInfoSearchFeignClient;

    /**
     * 用户属性服务
     */
    private DiskUserAttrService diskUserAttrService;

    @Autowired(required = false)
    public void setDiskUserInfoConvert(DiskUserInfoConvert diskUserInfoConvert) {
        this.diskUserInfoConvert = diskUserInfoConvert;
    }

    @Autowired
    public void setSysUserRoleManager(SysUserRoleManager sysUserRoleManager) {
        this.sysUserRoleManager = sysUserRoleManager;
    }

    @Autowired
    public void setUserInfoSearchFeignClient(UserInfoSearchFeignClient userInfoSearchFeignClient) {
        this.userInfoSearchFeignClient = userInfoSearchFeignClient;
    }

    @Autowired
    public void setDiskUserAttrService(DiskUserAttrService diskUserAttrService) {
        this.diskUserAttrService = diskUserAttrService;
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
                if (userInfo == null || session.getAttribute(ConstantConfig.Cache.USER_CONFIDENTIAL) == null) {
                    String username = (String) SecurityUtils.getSubject().getPrincipal();
                    // 通过登陆的 用户名 查找对应的用户信息
                    Result<UsernameSearchResponseDTO> usernameSearchResult = userInfoSearchFeignClient.usernameSearch(username);
                    if (Result.isNotSuccess(usernameSearchResult)) {
                        return null;
                    }
                    userInfo = diskUserInfoConvert.usernameSearchResponseConvert(usernameSearchResult.getData());
                    // 获取当前登录用户所有角色、权限、属性等信息
                    userInfo.setRoleInfo(sysUserRoleManager.listSysUserRoleInfo(userInfo.getBusinessId()));
                    userInfo.setUserAttr(diskUserAttrService.listDiskUserAttr(userInfo.getBusinessId()));
                    session.setAttribute(ConstantConfig.Cache.USERINFO_CACHE, userInfo);
                    // 保存用户机密信息
                    UserConfidentialInfo userConfidential = diskUserInfoConvert.usernameSearchResponseConvertUserConfidential(usernameSearchResult.getData());
                    session.setAttribute(ConstantConfig.Cache.USER_CONFIDENTIAL, userConfidential);
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

    /**
     * 从session中获取当前登录的用户一些涉密数据信息，无法获取时会抛出业务异常
     *
     * @return 用户一些涉密数据
     */
    @Override
    public UserConfidentialInfo getUserConfidentialToSessionException() {
        try {
            if (!SecurityUtils.getSubject().isAuthenticated()) {
                BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.USER_UN_LOGIN);
            }
            Subject subject = SecurityUtils.getSubject();
            Session session = subject.getSession(true);
            // 从缓存中拿到用户一些涉密数据
            UserConfidentialInfo userConfidential = (UserConfidentialInfo) session.getAttribute(ConstantConfig.Cache.USER_CONFIDENTIAL);
            if (userConfidential == null) {
                BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.INVALID_USERINFO);
            }
            return userConfidential;
        } catch (Exception e) {
            throw new BusinessException(StatusCodeEnum.INVALID_USERINFO);
        }
    }

    /**
     * 从session中获取当前登录的用户信息数据模型，无法获取时会抛出业务异常
     *
     * @return 通用的用户信息数据模型
     */
    @Override
    public DiskUserModel getUserInfoToSessionException() {
        // 从缓存中获取用户信息对象
        UserLoginResponseDTO userLoginResponseDTO = this.getUserInfoToSession();
        // 无法获取用户信息时
        if (userLoginResponseDTO == null || userLoginResponseDTO.getUserInfo() == null) {
            BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.INVALID_USERINFO);
        }
        return userLoginResponseDTO.getUserInfo();
    }
}
