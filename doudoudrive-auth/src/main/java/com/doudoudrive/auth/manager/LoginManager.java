package com.doudoudrive.auth.manager;

import com.doudoudrive.common.model.dto.model.DiskUserModel;
import com.doudoudrive.common.model.dto.model.UserConfidentialInfo;
import com.doudoudrive.common.model.dto.response.UserLoginResponseDTO;

/**
 * <p>登录鉴权服务通用业务处理层接口</p>
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

    /**
     * 从session中获取当前登录的用户一些涉密数据信息，无法获取时会抛出业务异常
     *
     * @return 用户一些涉密数据
     */
    UserConfidentialInfo getUserConfidentialToSessionException();

    /**
     * 从session中获取当前登录的用户信息数据模型，无法获取时会抛出业务异常
     *
     * @return 通用的用户信息数据模型
     */
    DiskUserModel getUserInfoToSessionException();

    /**
     * 从session中获取当前登录的用户token信息
     *
     * @return 用户token字符串
     */
    String getUserToken();

    /**
     * 尝试根据token去获取指定的会话信息，无法获取时返回null
     *
     * @param token 用户token
     * @return 通用的用户信息数据模型
     */
    DiskUserModel getUserInfoToToken(String token);

    /**
     * 尝试根据token去获取指定的用户会话信息，无法获取时会尝试从当前session中获取，无法获取时返回null
     *
     * @param token 用户token
     * @return 返回用户登录模块响应数据DTO模型
     */
    UserLoginResponseDTO getUserInfoToTokenSession(String token);

    /**
     * 尝试去更新指定用户的会话缓存信息
     *
     * @param token    需要更新的用户token
     * @param userInfo 当前需要更新缓存的用户数据
     */
    void attemptUpdateUserSession(String token, DiskUserModel userInfo);

}
