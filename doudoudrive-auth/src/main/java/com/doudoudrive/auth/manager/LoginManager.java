package com.doudoudrive.auth.manager;

import com.doudoudrive.common.model.dto.response.UserLoginResponseDTO;

/**
 * <p>登录服务通用业务处理层接口</p>
 * <p>2022-04-05 18:39</p>
 *
 * @author Dan
 **/
public interface LoginManager {

    /**
     * 从session中获取当前登录的用户信息
     *
     * @return 返回用户登录模块响应数据DTO模型
     */
    UserLoginResponseDTO getUserInfoToSession();

}
