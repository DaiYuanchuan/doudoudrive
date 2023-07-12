package com.doudoudrive.file.manager;

import com.doudoudrive.common.constant.ConstantConfig;

import java.math.BigDecimal;
import java.util.Map;

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

    /**
     * 查找指定用户的指定属性的值，直接从数据库中查询
     *
     * @param userId   用户标识
     * @param attrName 属性名
     * @return 返回查找到的用户属性值
     */
    BigDecimal getUserAttrValue(String userId, ConstantConfig.UserAttrEnum attrName);

    /**
     * 原子性服务，扣除指定字段的数量
     *
     * @param userId       需要进行操作的用户标识
     * @param userAttrEnum 需要扣除的字段属性枚举值
     * @param size         需要扣除的数量
     * @return 返回修改的条数，根据返回值判断是否修改成功
     */
    Integer deducted(String userId, ConstantConfig.UserAttrEnum userAttrEnum, String size);

    /**
     * 原子性服务，增加指定字段的数量
     *
     * @param userId       需要进行操作的用户标识
     * @param userAttrEnum 需要增加的字段属性枚举值
     * @param size         需要增加的数量
     * @param upperLimit   增加的上限
     * @return 返回修改的条数，根据返回值判断是否修改成功
     */
    Integer increase(String userId, ConstantConfig.UserAttrEnum userAttrEnum, String size, String upperLimit);

    /**
     * 根据用户标识查询指定用户下所有属性信息
     *
     * @param userId 根据用户业务id查找
     * @return 返回查找到的用户属性数据Map对象
     */
    Map<String, String> listDiskUserAttr(String userId);

}
