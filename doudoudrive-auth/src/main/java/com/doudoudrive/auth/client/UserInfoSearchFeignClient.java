package com.doudoudrive.auth.client;

import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.model.dto.request.DeleteElasticsearchUserInfoRequestDTO;
import com.doudoudrive.common.model.dto.request.SaveElasticsearchUserInfoRequestDTO;
import com.doudoudrive.common.model.dto.request.UpdateElasticsearchUserInfoRequestDTO;
import com.doudoudrive.common.model.dto.response.UsernameSearchResponseDTO;
import com.doudoudrive.common.util.http.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * <p>用户信息搜索服务Feign调用</p>
 * <p>2022-03-21 15:36</p>
 *
 * @author Dan
 **/
@FeignClient(name = "searchServer", contextId = "userInfoSearchFeignClient")
public interface UserInfoSearchFeignClient {

    /**
     * 在es中保存用户信息
     *
     * @param requestDTO 保存es用户信息时的请求数据模型
     * @return 通用状态返回类
     */
    @PostMapping(value = "/search/userinfo/save", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    Result<?> saveElasticsearchUserInfo(@RequestBody SaveElasticsearchUserInfoRequestDTO requestDTO);

    /**
     * 删除es中保存的用户信息
     *
     * @param requestDTO 删除es用户信息时的请求数据模型
     * @return 通用状态返回类
     */
    @PostMapping(value = "/search/userinfo/delete", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    Result<?> deleteElasticsearchUserInfo(@RequestBody DeleteElasticsearchUserInfoRequestDTO requestDTO);

    /**
     * 更新es中保存的用户信息
     *
     * @param requestDTO 更新es用户信息时的请求数据模型
     * @return 通用状态返回类
     */
    @PostMapping(value = "/search/userinfo/update", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    Result<?> updateElasticsearchUserInfo(@RequestBody UpdateElasticsearchUserInfoRequestDTO requestDTO);

    /**
     * 用户登录信息搜索
     * <pre>
     *     根据用户名、用户邮箱、用户手机号进行精确搜索
     * </pre>
     *
     * @param username 用户登录的用户名(用户名、用户邮箱、用户手机号)
     * @return 用户实体信息ES数据模型
     */
    @GetMapping(value = "/search/userinfo/username-search", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    Result<UsernameSearchResponseDTO> usernameSearch(@RequestParam(value = "username", defaultValue = "") String username);

    /**
     * 查询用户关键信息是否存在
     *
     * @param username  用户名
     * @param userEmail 用户邮箱
     * @param userTel   用户手机号
     * @return 如果用户关键信息存在则会响应异常的状态码，否则响应成功
     */
    @GetMapping(value = "/search/userinfo/necessary-search", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    Result<String> userInfoKeyExistsSearch(@RequestParam(value = "username", defaultValue = "") String username,
                                           @RequestParam(value = "userEmail", defaultValue = "") String userEmail,
                                           @RequestParam(value = "userTel", defaultValue = "") String userTel);
}