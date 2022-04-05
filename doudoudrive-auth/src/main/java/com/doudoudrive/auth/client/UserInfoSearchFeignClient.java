package com.doudoudrive.auth.client;

import com.doudoudrive.common.model.dto.response.UsernameSearchResponseDTO;
import com.doudoudrive.common.util.http.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * <p>用户信息搜索服务Feign调用</p>
 * <p>2022-03-21 15:36</p>
 *
 * @author Dan
 **/
@FeignClient(name = "searchServer", contextId = "authSearchFeignClient")
public interface UserInfoSearchFeignClient {

    /**
     * 用户登录信息搜索
     * <pre>
     *     根据用户名、用户邮箱、用户手机号进行精确搜索
     * </pre>
     *
     * @param username 用户登录的用户名(用户名、用户邮箱、用户手机号)
     * @return 用户实体信息ES数据模型
     */
    @GetMapping(value = "/search/userinfo/username-search", produces = "application/json;charset=UTF-8")
    Result<UsernameSearchResponseDTO> usernameSearch(@RequestParam(value = "username", defaultValue = "") String username);
}