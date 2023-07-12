package com.doudoudrive.userinfo.manager.impl;

import com.doudoudrive.auth.client.UserInfoSearchFeignClient;
import com.doudoudrive.auth.manager.LoginManager;
import com.doudoudrive.auth.manager.SysUserRoleManager;
import com.doudoudrive.auth.util.EncryptionUtil;
import com.doudoudrive.common.cache.lock.RedisLockManager;
import com.doudoudrive.common.constant.*;
import com.doudoudrive.common.global.BusinessExceptionUtil;
import com.doudoudrive.common.model.convert.DiskUserInfoConvert;
import com.doudoudrive.common.model.dto.model.SecretSaltingInfo;
import com.doudoudrive.common.model.dto.model.SysUserRoleModel;
import com.doudoudrive.common.model.dto.request.SaveUserInfoRequestDTO;
import com.doudoudrive.common.model.dto.request.UpdateElasticsearchUserInfoRequestDTO;
import com.doudoudrive.common.model.dto.response.UserLoginResponseDTO;
import com.doudoudrive.common.model.pojo.DiskUser;
import com.doudoudrive.common.util.http.Result;
import com.doudoudrive.common.util.lang.SequenceUtil;
import com.doudoudrive.commonservice.constant.TransactionManagerConstant;
import com.doudoudrive.commonservice.service.DiskDictionaryService;
import com.doudoudrive.commonservice.service.DiskUserAttrService;
import com.doudoudrive.commonservice.service.DiskUserService;
import com.doudoudrive.userinfo.manager.UserInfoManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * <p>用户信息服务的通用业务处理层接口实现</p>
 * <p>2022-03-21 18:13</p>
 *
 * @author Dan
 **/
@Service("userInfoManager")
public class UserInfoManagerImpl implements UserInfoManager {

    private DiskUserService diskUserService;

    private UserInfoSearchFeignClient userInfoSearchFeignClient;

    private DiskUserInfoConvert diskUserInfoConvert;

    /**
     * 数据字典模块服务
     */
    private DiskDictionaryService diskDictionaryService;

    /**
     * 用户属性模块
     */
    private DiskUserAttrService diskUserAttrService;

    /**
     * 用户、角色关联模块
     */
    private SysUserRoleManager sysUserRoleManager;

    /**
     * redis分布式锁管理器
     */
    private RedisLockManager redisLockManager;

    /**
     * 登录鉴权服务
     */
    private LoginManager loginManager;

    @Autowired
    public void setDiskUserService(DiskUserService diskUserService) {
        this.diskUserService = diskUserService;
    }

    @Autowired
    public void setUserInfoSearchFeignClient(UserInfoSearchFeignClient userInfoSearchFeignClient) {
        this.userInfoSearchFeignClient = userInfoSearchFeignClient;
    }

    @Autowired(required = false)
    public void setDiskUserInfoConvert(DiskUserInfoConvert diskUserInfoConvert) {
        this.diskUserInfoConvert = diskUserInfoConvert;
    }

    @Autowired
    public void setDiskDictionaryService(DiskDictionaryService diskDictionaryService) {
        this.diskDictionaryService = diskDictionaryService;
    }

    @Autowired
    public void setDiskUserAttrService(DiskUserAttrService diskUserAttrService) {
        this.diskUserAttrService = diskUserAttrService;
    }

    @Autowired
    public void setSysUserRoleManager(SysUserRoleManager sysUserRoleManager) {
        this.sysUserRoleManager = sysUserRoleManager;
    }

    @Autowired
    public void setRedisLockManager(RedisLockManager redisLockManager) {
        this.redisLockManager = redisLockManager;
    }

    @Autowired
    public void setLoginManager(LoginManager loginManager) {
        this.loginManager = loginManager;
    }

    /**
     * 保存用户信息服务
     *
     * @param saveUserInfoRequestDTO 保存用户信息时的请求数据模型
     */
    @Override
    @Transactional(rollbackFor = Exception.class, value = TransactionManagerConstant.USERINFO_TRANSACTION_MANAGER)
    public void insert(SaveUserInfoRequestDTO saveUserInfoRequestDTO) {
        // 获取用户默认头像
        String avatar = diskDictionaryService.getDictionary(DictionaryConstant.DEFAULT_AVATAR, String.class);

        // 对明文密码进行加盐加密
        SecretSaltingInfo saltingInfo = EncryptionUtil.secretSalting(saveUserInfoRequestDTO.getUserPwd());
        // 生成用户实体信息
        DiskUser diskUserInfo = diskUserInfoConvert.saveUserInfoRequestConvert(saveUserInfoRequestDTO, saltingInfo, avatar);
        // 生成业务id
        diskUserInfo.setBusinessId(SequenceUtil.nextId(SequenceModuleEnum.DISK_USER));

        // 保存用户实体信息
        diskUserService.insert(diskUserInfo);
        // 为新用户绑定用户属性信息
        diskUserAttrService.insertBatch(ConstantConfig.UserAttrEnum.builderList(diskUserInfo.getBusinessId()));
        // 为新用户绑定默认角色信息
        List<RoleCodeEnum> roleList = RoleCodeEnum.builderList(Boolean.TRUE);
        // 如果手机号不为空，则为新用户绑定可分享的角色
        if (StringUtils.isNotBlank(saveUserInfoRequestDTO.getUserTel())) {
            roleList.add(RoleCodeEnum.FILE_SHARE);
        }
        sysUserRoleManager.insert(diskUserInfo.getBusinessId(), roleList);

        // 获取表格后缀
        String tableSuffix = SequenceUtil.tableSuffix(diskUserInfo.getBusinessId(), ConstantConfig.TableSuffix.USERINFO);
        // 用户信息先入库，然后入es
        Result<?> saveElasticsearchResult = userInfoSearchFeignClient.saveElasticsearchUserInfo(diskUserInfoConvert.diskUserInfoConvert(diskUserInfo, tableSuffix));
        if (Result.isNotSuccess(saveElasticsearchResult)) {
            BusinessExceptionUtil.throwBusinessException(saveElasticsearchResult);
        }
    }

    /**
     * 更新用户的基本信息，针对用户基础信息的更新(不包含用户权限、属性类的更新)
     *
     * @param userinfo 需要更新的用户信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class, value = TransactionManagerConstant.USERINFO_TRANSACTION_MANAGER)
    public void updateBasicsInfo(DiskUser userinfo) {
        // 密码不为空时，需要对新密码加盐加密
        if (StringUtils.isNotBlank(userinfo.getUserPwd())) {
            SecretSaltingInfo saltingInfo = EncryptionUtil.secretSalting(userinfo.getUserPwd());
            userinfo.setUserPwd(saltingInfo.getPassword());
            userinfo.setUserSalt(saltingInfo.getSalt());
        }

        // 如果需要修改邮箱或者手机号，需要先获取锁，避免并发时注册、修改操作冲突导致存在重复数据
        String lock = redisLockManager.lock(RedisLockEnum.USER_REGISTER.getLockName());
        try {
            // 邮箱或者手机号不为空时，需要查询邮箱、手机号信息是否存在
            if (!StringUtils.isAllBlank(userinfo.getUserEmail(), userinfo.getUserTel())) {

                // 查询用户关键信息是否存在
                Result<String> userInfoKeyExistsSearchResult = userInfoSearchFeignClient
                        .userInfoKeyExistsSearch(null, userinfo.getUserEmail(), userinfo.getUserTel());
                if (Result.isNotSuccess(userInfoKeyExistsSearchResult)) {
                    BusinessExceptionUtil.throwBusinessException(userInfoKeyExistsSearchResult);
                }

                // 如果手机号不为空，判断当前用户是否具有分享权限，如果没有则为用户绑定分享权限
                if (StringUtils.isNotBlank(userinfo.getUserTel())) {
                    // 获取指定用户下所有的角色信息
                    List<SysUserRoleModel> sysUserRoleList = sysUserRoleManager.listSysUserRoleInfo(userinfo.getBusinessId());
                    // 获取所有的角色编码
                    List<String> roleCodeList = sysUserRoleList.stream().map(SysUserRoleModel::getRoleCode).toList();

                    // 判断当前用户是否具有分享权限
                    if (!roleCodeList.contains(RoleCodeEnum.FILE_SHARE.getRoleCode())) {
                        // 为用户绑定文件分享角色
                        sysUserRoleManager.insert(userinfo.getBusinessId(), Collections.singletonList(RoleCodeEnum.FILE_SHARE));
                    }
                }
            }

            // 执行更新操作
            Integer update = diskUserService.update(userinfo);
            // 如果更新成功，需要更新es中的用户信息
            if (update > NumberConstant.INTEGER_ZERO) {
                // 用户信息先入库，然后入es
                Result<?> updateElasticsearchResult = userInfoSearchFeignClient.updateElasticsearchUserInfo(diskUserInfoConvert.diskUserConvert(userinfo));
                if (Result.isNotSuccess(updateElasticsearchResult)) {
                    BusinessExceptionUtil.throwBusinessException(updateElasticsearchResult);
                }
            }
        } finally {
            // 释放锁
            redisLockManager.unlock(RedisLockEnum.USER_REGISTER.getLockName(), lock);
        }
        // 刷新用户session信息
        this.refreshUserSession(userinfo.getBusinessId());
    }

    /**
     * 重置用户密码
     *
     * @param businessId 用户系统内唯一标识
     * @param password   用户需要修改的新密码
     */
    @Override
    @Transactional(rollbackFor = Exception.class, value = TransactionManagerConstant.USERINFO_TRANSACTION_MANAGER)
    public void resetPassword(String businessId, String password) {
        // 对明文密码进行加盐加密
        SecretSaltingInfo saltingInfo = EncryptionUtil.secretSalting(password);
        // 先修改数据库信息
        Integer integer = diskUserService.update(DiskUser.builder()
                .businessId(businessId)
                .userPwd(saltingInfo.getPassword())
                .userSalt(saltingInfo.getSalt())
                .build());
        if (integer > NumberConstant.INTEGER_ZERO) {
            // 数据库修改成功后再修改es数据
            Result<?> result = userInfoSearchFeignClient.updateElasticsearchUserInfo(UpdateElasticsearchUserInfoRequestDTO.builder()
                    .businessId(businessId)
                    .userPwd(saltingInfo.getPassword())
                    .userSalt(saltingInfo.getSalt())
                    .build());
            if (Result.isNotSuccess(result)) {
                BusinessExceptionUtil.throwBusinessException(result);
            }
        }
    }

    /**
     * 尝试刷新指定用户的会话缓存信息
     *
     * @param businessId 用户系统内唯一标识
     */
    private void refreshUserSession(String businessId) {
        // 从当前请求中获取当前用户的登录请求信息
        UserLoginResponseDTO userLoginResponse = loginManager.getUserInfoToSession();
        if (userLoginResponse == null || StringUtils.isBlank(userLoginResponse.getToken())) {
            return;
        }

        // 获取需要刷新的用户信息
        Optional.ofNullable(diskUserInfoConvert.diskUserConvertUserModel(diskUserService.getDiskUser(businessId))).ifPresent(userinfo -> {
            // 获取当前用户的所有角色、权限、属性等信息
            userinfo.setRoleInfo(sysUserRoleManager.listSysUserRoleInfo(userinfo.getBusinessId()));
            userinfo.setUserAttr(diskUserAttrService.listDiskUserAttr(userinfo.getBusinessId()));

            // 尝试去更新指定用户的会话缓存信息
            loginManager.attemptUpdateUserSession(userLoginResponse.getToken(), userinfo);
        });
    }
}
