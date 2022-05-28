package com.doudoudrive.file.manager.impl;

import com.doudoudrive.auth.manager.LoginManager;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.model.dto.model.DiskUserModel;
import com.doudoudrive.commonservice.service.DiskUserAttrService;
import com.doudoudrive.file.manager.DiskUserAttrManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * <p>用户属性服务的通用业务处理层接口实现</p>
 * <p>2022-05-28 20:45</p>
 *
 * @author Dan
 **/
@Slf4j
@Service("diskUserAttrManager")
public class DiskUserAttrManagerImpl implements DiskUserAttrManager {

    private LoginManager loginManager;

    private DiskUserAttrService diskUserAttrService;

    @Autowired
    public void setLoginManager(LoginManager loginManager) {
        this.loginManager = loginManager;
    }

    @Autowired
    public void setDiskUserAttrService(DiskUserAttrService diskUserAttrService) {
        this.diskUserAttrService = diskUserAttrService;
    }

    /**
     * 查找指定用户的指定属性的值
     * 先从当前用户缓存中查询，找不到时从数据库中查询
     *
     * @param token    用户登录时的token
     * @param userId   用户标识
     * @param attrName 属性名
     * @return 返回查找到的用户属性值
     */
    @Override
    public BigDecimal getUserAttrValue(String token, String userId, ConstantConfig.UserAttrEnum attrName) {
        // 先通过用户token查询用户当前登录信息
        DiskUserModel userInfo = loginManager.getUserInfoToToken(token);
        if (userInfo != null) {
            // 获取用户属性缓存Map
            return new BigDecimal(userInfo.getUserAttr().get(attrName.param));
        }
        return diskUserAttrService.getDiskUserAttrValue(userId, attrName);
    }
}
