package com.doudoudrive.userinfo.manager;

import com.doudoudrive.common.model.dto.request.SaveUserInfoRequestDTO;

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

}
