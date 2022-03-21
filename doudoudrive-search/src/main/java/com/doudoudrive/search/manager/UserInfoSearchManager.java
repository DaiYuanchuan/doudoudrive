package com.doudoudrive.search.manager;

import com.doudoudrive.search.model.elasticsearch.UserInfoDTO;

/**
 * <p>用户信息搜索服务的通用业务处理层接口</p>
 * <p>2022-03-20 22:52</p>
 *
 * @author Dan
 **/
public interface UserInfoSearchManager {

    /**
     * 保存用户信息，es中保存用户信息
     * <pre>
     *     先入库，然后再往es里存用户信息
     * </pre>
     *
     * @param userInfoDTO 用户实体信息ES数据模型
     */
    void saveUserInfo(UserInfoDTO userInfoDTO);

    /**
     * 根据用户系统内唯一标识删除指定用户信息
     * <pre>
     *     先删库，然后再删es里存的用户信息
     * </pre>
     *
     * @param id 用户系统内唯一标识
     */
    void deleteUserInfo(String id);

    /**
     * 根据用户系统内唯一标识去更新用户信息
     *
     * @param id          用户系统内唯一标识
     * @param userInfoDTO 用户实体信息ES数据模型
     */
    void updateUserInfo(String id, UserInfoDTO userInfoDTO);

    /**
     * 用户登录信息搜索
     * <pre>
     *     根据用户名、用户邮箱、用户手机号进行精确搜索
     * </pre>
     *
     * @param username 用户登录的用户名(用户名、用户邮箱、用户手机号)
     * @return 用户实体信息ES数据模型
     */
    UserInfoDTO userLoginInfoSearch(String username);

}
