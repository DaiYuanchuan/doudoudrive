package com.doudoudrive.auth.manager;

import com.doudoudrive.auth.model.dto.UserInfoDTO;
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

    /**
     * 用户登录信息搜索，此接口专为登录鉴权时使用，其他场景请调用搜索服务接口
     * <pre>
     *     根据用户名、用户邮箱、用户手机号进行精确搜索
     * </pre>
     *
     * @param username 用户登录的用户名(用户名、用户邮箱、用户手机号)
     * @return 用户实体信息ES数据模型
     */
    UserInfoDTO usernameSearch(String username);

}
