package com.doudoudrive.commonservice.service;

import com.doudoudrive.common.model.pojo.DiskUser;

/**
 * <p>用户模块服务层接口</p>
 * <p>2022-03-04 12:49</p>
 *
 * @author Dan
 **/
public interface DiskUserService {

    /**
     * 新增用户模块
     *
     * @param diskUser 需要新增的用户模块实体
     */
    void insert(DiskUser diskUser);

    /**
     * 删除用户模块
     *
     * @param businessId 根据业务id(businessId)删除数据
     * @return 返回删除的条数
     */
    Integer delete(String businessId);

    /**
     * 修改用户模块
     *
     * @param diskUser 需要进行修改的用户模块实体
     * @return 返回修改的条数
     */
    Integer update(DiskUser diskUser);

    /**
     * 根据用户业务标识查找指定用户信息
     *
     * @param businessId 根据业务id(businessId)查找
     * @return 返回查找到的用户信息实体(实体中会返回一些涉密字段 ， 不可直接抛给前端)
     */
    DiskUser getDiskUser(String businessId);
}
