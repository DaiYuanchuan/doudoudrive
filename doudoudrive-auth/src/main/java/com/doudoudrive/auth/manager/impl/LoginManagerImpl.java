package com.doudoudrive.auth.manager.impl;

import com.doudoudrive.auth.client.UserInfoSearchFeignClient;
import com.doudoudrive.auth.config.ShiroRealm;
import com.doudoudrive.auth.manager.LoginManager;
import com.doudoudrive.auth.manager.SysUserRoleManager;
import com.doudoudrive.common.cache.RedisTemplateClient;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.global.BusinessException;
import com.doudoudrive.common.global.BusinessExceptionUtil;
import com.doudoudrive.common.global.StatusCodeEnum;
import com.doudoudrive.common.model.convert.DiskUserInfoConvert;
import com.doudoudrive.common.model.dto.model.CacheRefreshModel;
import com.doudoudrive.common.model.dto.model.DiskUserModel;
import com.doudoudrive.common.model.dto.model.UserConfidentialInfo;
import com.doudoudrive.common.model.dto.model.auth.FileVisitorAuthModel;
import com.doudoudrive.common.model.dto.response.UserLoginResponseDTO;
import com.doudoudrive.common.model.dto.response.UserinfoSearchResponseDTO;
import com.doudoudrive.common.util.http.Result;
import com.doudoudrive.commonservice.service.DiskDictionaryService;
import com.doudoudrive.commonservice.service.DiskUserAttrService;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.DefaultSessionKey;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    private DiskUserAttrService diskUserAttrService;
    private RedisTemplateClient redisTemplateClient;
    private DiskDictionaryService diskDictionaryService;

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

    @Autowired
    public void setRedisTemplateClient(RedisTemplateClient redisTemplateClient) {
        this.redisTemplateClient = redisTemplateClient;
    }

    @Autowired
    public void setDiskDictionaryService(DiskDictionaryService diskDictionaryService) {
        this.diskDictionaryService = diskDictionaryService;
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
                    this.assignsUserSessionCache(session, userInfoSearchFeignClient.usernameSearch(username));
                    userInfo = (DiskUserModel) session.getAttribute(ConstantConfig.Cache.USERINFO_CACHE);
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

    /**
     * 从session中获取当前登录的用户token信息
     *
     * @return 用户token字符串
     */
    @Override
    public String getUserToken() {
        // 从缓存中获取用户信息对象
        UserLoginResponseDTO userLoginResponseDTO = this.getUserInfoToSession();
        // 无法获取用户信息时
        if (userLoginResponseDTO == null || userLoginResponseDTO.getUserInfo() == null) {
            BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.INVALID_USERINFO);
        }
        return userLoginResponseDTO.getToken();
    }

    /**
     * 尝试根据token去获取指定的会话信息，无法获取时返回null
     *
     * @param token 用户token
     * @return 通用的用户信息数据模型
     */
    @Override
    public DiskUserModel getUserInfoToToken(String token) {
        if (StringUtils.isBlank(token)) {
            return null;
        }
        try {
            // 通过token尝试获取用户的session对象
            Session session = SecurityUtils.getSecurityManager().getSession(new DefaultSessionKey(token));
            return (DiskUserModel) session.getAttribute(ConstantConfig.Cache.USERINFO_CACHE);
        } catch (UnknownSessionException e) {
            return null;
        }
    }

    /**
     * 尝试根据token去获取指定的用户会话信息，无法获取时会尝试从当前session中获取，无法获取时返回null
     *
     * @param token 用户token
     * @return 返回用户登录模块响应数据DTO模型
     */
    @Override
    public UserLoginResponseDTO getUserInfoToTokenSession(String token) {
        // 首先会尝试从session中获取用户信息
        UserLoginResponseDTO loginResponse = this.getUserInfoToSession();
        if (loginResponse == null) {
            // 无法获取时尝试从指定token中获取
            DiskUserModel userInfoToToken = this.getUserInfoToToken(token);
            if (userInfoToToken == null) {
                return null;
            }
            // 如果能从token中获取到用户信息，则构建用户登录模块响应数据DTO模型
            return UserLoginResponseDTO.builder()
                    .userInfo(userInfoToToken)
                    .token(token)
                    .build();
        }
        return loginResponse;
    }

    /**
     * 尝试去更新指定用户的会话缓存信息，忽略出现的异常
     *
     * @param token  需要更新的用户token
     * @param userId 当前需要更新缓存信息的用户标识
     */
    @Override
    public void attemptUpdateUserSession(String token, String userId) {
        if (!StringUtils.isAnyBlank(token, userId)) {
            try {
                // 通过token尝试获取用户的session对象
                Session session = SecurityUtils.getSecurityManager().getSession(new DefaultSessionKey(token));
                // 重新分配用户会话缓存信息
                assignsUserSessionCache(session, userInfoSearchFeignClient.userIdQuery(userId));

                // 清除session里面权限信息
                session.removeAttribute(ConstantConfig.Cache.USER_ROLE_CACHE);

                // 当前登录的用户名
                String username = (String) SecurityUtils.getSubject().getPrincipal();

                // 清除旧的用户权限信息缓存
                DefaultWebSecurityManager securityManager = (DefaultWebSecurityManager) SecurityUtils.getSecurityManager();
                ShiroRealm shiroRealm = (ShiroRealm) securityManager.getRealms().iterator().next();
                shiroRealm.getAuthorizationCache().remove(username);

                // 需要清理的缓存key
                List<String> cacheKey = Lists.newArrayListWithExpectedSize(NumberConstant.INTEGER_TWO);
                // session内容
                cacheKey.add(ConstantConfig.Cache.DEFAULT_CACHE_KEY_PREFIX + token);
                // 用户权限信息
                cacheKey.add(ConstantConfig.Cache.DEFAULT_CACHE_REALM_PREFIX + username);

                // 更新完本地缓存后，需要通知到其他服务同步更新
                redisTemplateClient.publish(ConstantConfig.Cache.ChanelEnum.CHANNEL_CACHE, CacheRefreshModel.builder()
                        .cacheKey(cacheKey)
                        .build());
            } catch (UnknownSessionException ignored) {
            }
        }
    }

    /**
     * 分配用户session缓存字段信息
     *
     * @param session              会话对象
     * @param userinfoSearchResult 需要赋值的用户信息
     */
    private void assignsUserSessionCache(Session session, Result<UserinfoSearchResponseDTO> userinfoSearchResult) {
        if (Result.isNotSuccess(userinfoSearchResult)) {
            return;
        }

        // 保存用户机密信息
        UserConfidentialInfo userConfidential = diskUserInfoConvert.usernameSearchResponseConvertUserConfidential(userinfoSearchResult.getData());
        session.setAttribute(ConstantConfig.Cache.USER_CONFIDENTIAL, userConfidential);
        // 保存用户基本信息
        DiskUserModel userInfo = diskUserInfoConvert.usernameSearchResponseConvert(userinfoSearchResult.getData());
        // 生成文件访问密钥信息
        userInfo.setFileAccessKey(diskDictionaryService.encrypt(FileVisitorAuthModel.builder()
                .userId(userInfo.getBusinessId())
                .username(userInfo.getUserName())
                .secretKey(userConfidential.getSecretKey())
                .build()));

        // 获取当前登录用户所有角色、权限、属性等信息
        userInfo.setRoleInfo(sysUserRoleManager.listSysUserRoleInfo(userInfo.getBusinessId()));
        userInfo.setUserAttr(diskUserAttrService.listDiskUserAttr(userInfo.getBusinessId()));
        session.setAttribute(ConstantConfig.Cache.USERINFO_CACHE, userInfo);
    }
}
