package com.doudoudrive.file.manager;

import com.doudoudrive.common.constant.ConstantConfig;

import java.math.BigDecimal;

/**
 * <p>用户属性服务的通用业务处理层接口</p>
 * <p>2022-05-28 20:45</p>
 *
 * @author Dan
 **/
public interface DiskUserAttrManager {

    /**
     * 查找指定用户的指定属性的值
     * 先从当前用户缓存中查询，找不到时从数据库中查询，数据库中也找不到时返回枚举中的默认值
     *
     * @param token    用户登录时的token
     * @param userId   用户标识
     * @param attrName 属性名
     * @return 返回查找到的用户属性值
     */
    BigDecimal getUserAttrValue(String token, String userId, ConstantConfig.UserAttrEnum attrName);

}
