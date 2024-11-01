package com.doudoudrive.commonservice.service;

import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.model.pojo.DiskUserAttr;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>用户属性模块服务层接口</p>
 * <p>2022-05-12 14:52</p>
 *
 * @author Dan
 **/
public interface DiskUserAttrService {

    /**
     * 新增用户属性模块
     *
     * @param diskUserAttr 需要新增的用户属性模块实体
     */
    void insert(DiskUserAttr diskUserAttr);

    /**
     * 批量新增用户属性模块
     *
     * @param list 需要新增的用户属性模块集合
     */
    void insertBatch(List<DiskUserAttr> list);

    /**
     * 删除指定用户所有属性数据
     *
     * @param userId 根据用户业务id删除数据
     * @return 返回删除的条数
     */
    Integer deleteUserAttr(String userId);

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
     * 查找指定用户的指定属性的值
     *
     * @param userId   根据用户业务id查找
     * @param attrName 属性名
     * @return 返回查找到的用户属性值
     */
    BigDecimal getDiskUserAttrValue(String userId, ConstantConfig.UserAttrEnum attrName);

    /**
     * 根据用户标识查询指定用户下所有属性信息
     *
     * @param userId 根据用户业务id查找
     * @return 返回查找到的用户属性数据Map对象
     */
    Map<String, String> listDiskUserAttr(String userId);

}
