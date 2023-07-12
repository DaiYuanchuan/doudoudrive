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
import java.util.Map;

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
            return new BigDecimal(userInfo.getUserAttr().get(attrName.getParam()));
        }
        return this.getUserAttrValue(userId, attrName);
    }

    /**
     * 查找指定用户的指定属性的值，直接从数据库中查询
     *
     * @param userId   用户标识
     * @param attrName 属性名
     * @return 返回查找到的用户属性值
     */
    @Override
    public BigDecimal getUserAttrValue(String userId, ConstantConfig.UserAttrEnum attrName) {
        return diskUserAttrService.getDiskUserAttrValue(userId, attrName);
    }

    /**
     * 原子性服务，扣除指定字段的数量
     *
     * @param userId       需要进行操作的用户标识
     * @param userAttrEnum 需要扣除的字段属性枚举值
     * @param size         需要扣除的数量
     * @return 返回修改的条数，根据返回值判断是否修改成功
     */
    @Override
    public Integer deducted(String userId, ConstantConfig.UserAttrEnum userAttrEnum, String size) {
        return diskUserAttrService.deducted(userId, userAttrEnum, size);
    }

    /**
     * 原子性服务，增加指定字段的数量
     *
     * @param userId       需要进行操作的用户标识
     * @param userAttrEnum 需要增加的字段属性枚举值
     * @param size         需要增加的数量
     * @param upperLimit   增加的上限
     * @return 返回修改的条数，根据返回值判断是否修改成功
     */
    @Override
    public Integer increase(String userId, ConstantConfig.UserAttrEnum userAttrEnum, String size, String upperLimit) {
        return diskUserAttrService.increase(userId, userAttrEnum, size, upperLimit);
    }

    /**
     * 根据用户标识查询指定用户下所有属性信息
     *
     * @param userId 根据用户业务id查找
     * @return 返回查找到的用户属性数据Map对象
     */
    @Override
    public Map<String, String> listDiskUserAttr(String userId) {
        return diskUserAttrService.listDiskUserAttr(userId);
    }
}
