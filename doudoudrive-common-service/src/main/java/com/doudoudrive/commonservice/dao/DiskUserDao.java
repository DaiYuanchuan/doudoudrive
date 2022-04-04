package com.doudoudrive.commonservice.dao;

import com.doudoudrive.common.model.pojo.DiskUser;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>用户模块数据访问层</p>
 * <p>2022-03-04 12:37</p>
 *
 * @author Dan
 **/
@Repository
public interface DiskUserDao {

    /**
     * 新增用户模块
     *
     * @param diskUser 需要新增的用户模块实体
     * @return 返回新增的条数
     */
    Integer insert(@Param("diskUser") DiskUser diskUser);

    /**
     * 删除用户模块
     *
     * @param businessId 根据业务id(businessId)删除数据
     * @return 返回删除的条数
     */
    Integer delete(@Param("businessId") String businessId);

    /**
     * 修改用户模块
     *
     * @param diskUser 需要进行修改的用户模块实体
     * @return 返回修改的条数
     */
    Integer update(@Param("diskUser") DiskUser diskUser);

    /**
     * 查找用户模块
     *
     * @param businessId 根据业务id(businessId)查找
     * @return 返回查找到的用户模块实体
     */
    DiskUser getDiskUser(@Param("businessId") String businessId);
}
