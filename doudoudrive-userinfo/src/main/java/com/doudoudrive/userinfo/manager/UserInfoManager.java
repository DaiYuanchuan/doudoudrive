package com.doudoudrive.userinfo.manager;

import com.doudoudrive.common.model.dto.request.SaveUserInfoRequestDTO;
import com.doudoudrive.common.model.pojo.DiskUser;

/**
 * <p>用户信息服务的通用业务处理层接口</p>
 * <p>2022-03-21 18:12</p>
 *
 * @author Dan
 **/
public interface UserInfoManager {

    /**
     * 保存用户信息服务
     *
     * @param saveUserInfoRequestDTO 保存用户信息时的请求数据模型
     */
    void insert(SaveUserInfoRequestDTO saveUserInfoRequestDTO);

    /**
     * 更新用户的基本信息，针对用户基础信息的更新(不包含用户权限、属性类的更新)
     *
     * @param userinfo 需要更新的用户的基本信息
     */
    void updateBasicsInfo(DiskUser userinfo);

    /**
     * 重置用户密码
     *
     * @param businessId 用户系统内唯一标识
     * @param username   用户名
     * @param password   用户需要修改的新密码
     */
    void resetPassword(String businessId, String username, String password);

}
