package com.doudoudrive.userinfo.manager.impl;

import com.doudoudrive.auth.util.EncryptionUtil;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.SequenceModuleEnum;
import com.doudoudrive.common.global.BusinessExceptionUtil;
import com.doudoudrive.common.model.convert.DiskUserInfoConvert;
import com.doudoudrive.common.model.dto.model.SecretSaltingInfo;
import com.doudoudrive.common.model.dto.request.SaveUserInfoRequestDTO;
import com.doudoudrive.common.model.pojo.DiskUser;
import com.doudoudrive.common.util.http.Result;
import com.doudoudrive.common.util.lang.SequenceUtil;
import com.doudoudrive.commonservice.constant.TransactionManagerConstant;
import com.doudoudrive.commonservice.service.DiskUserService;
import com.doudoudrive.userinfo.client.UserInfoSearchFeignClient;
import com.doudoudrive.userinfo.manager.UserInfoManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * 保存用户信息服务
     *
     * @param saveUserInfoRequestDTO 保存用户信息时的请求数据模型
     */
    @Override
    @Transactional(rollbackFor = Exception.class, value = TransactionManagerConstant.USERINFO_TRANSACTION_MANAGER)
    public void insert(SaveUserInfoRequestDTO saveUserInfoRequestDTO) {
        // 对明文密码进行加盐加密
        SecretSaltingInfo saltingInfo = EncryptionUtil.secretSalting(saveUserInfoRequestDTO.getUserPwd());
        // 生成用户实体信息
        DiskUser diskUserInfo = diskUserInfoConvert.saveUserInfoRequestConvert(saveUserInfoRequestDTO, saltingInfo);
        // 先入库，然后入es
        diskUserInfo.setBusinessId(SequenceUtil.nextId(SequenceModuleEnum.DISK_USER));
        diskUserService.insert(diskUserInfo);

        // 获取表格后缀
        String tableSuffix = SequenceUtil.tableSuffix(diskUserInfo.getBusinessId(), ConstantConfig.TableSuffix.USERINFO);
        Result<?> saveElasticsearchResult = userInfoSearchFeignClient.saveElasticsearchUserInfo(diskUserInfoConvert.diskUserInfoConvert(diskUserInfo, tableSuffix));
        if (Result.isNotSuccess(saveElasticsearchResult)) {
            BusinessExceptionUtil.throwBusinessException(saveElasticsearchResult);
        }
    }
}
