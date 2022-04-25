package com.doudoudrive.sms.client;

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
@FeignClient(name = "searchServer", contextId = "userInfoSearchFeignClient")
public interface UserInfoSearchFeignClient {

    /**
     * 查询用户关键信息是否存在
     *
     * @param username  用户名
     * @param userEmail 用户邮箱
     * @param userTel   用户手机号
     * @return 如果用户关键信息存在则会响应异常的状态码，否则响应成功
     */
    @GetMapping(value = "/search/userinfo/necessary-search", produces = "application/json;charset=UTF-8")
    Result<String> userInfoKeyExistsSearch(@RequestParam(value = "username", defaultValue = "") String username,
                                           @RequestParam(value = "userEmail", defaultValue = "") String userEmail,
                                           @RequestParam(value = "userTel", defaultValue = "") String userTel);
}
