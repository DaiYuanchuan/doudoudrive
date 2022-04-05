package com.doudoudrive.userinfo.controller;

import cn.hutool.core.util.PhoneUtil;
import com.doudoudrive.common.annotation.OpLog;
import com.doudoudrive.common.global.StatusCodeEnum;
import com.doudoudrive.common.model.dto.request.SaveUserInfoRequestDTO;
import com.doudoudrive.common.util.http.Result;
import com.doudoudrive.userinfo.client.UserInfoSearchFeignClient;
import com.doudoudrive.userinfo.manager.UserInfoManager;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * <p>用户信息服务控制层实现</p>
 * <p>2022-03-21 18:07</p>
 *
 * @author Dan
 **/
@Slf4j
@Validated
@RestController
@RequestMapping(value = "/userinfo")
public class UserInfoController {

    private UserInfoSearchFeignClient userInfoSearchFeignClient;

    private UserInfoManager userInfoManager;

    @Autowired
    public void setUserInfoSearchFeignClient(UserInfoSearchFeignClient userInfoSearchFeignClient) {
        this.userInfoSearchFeignClient = userInfoSearchFeignClient;
    }

    @Autowired
    public void setUserInfoManager(UserInfoManager userInfoManager) {
        this.userInfoManager = userInfoManager;
    }

    @SneakyThrows
    @ResponseBody
    @OpLog(title = "保存用户信息", businessType = "用户信息配置中心")
    @PostMapping(value = "/save", produces = "application/json;charset=UTF-8")
    public Result<?> saveElasticsearchUserInfo(@RequestBody @Valid SaveUserInfoRequestDTO requestDTO,
                                               HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=UTF-8");
        // 手机号不为空，校验手机号格式是否正确
        if (StringUtils.isNotBlank(requestDTO.getUserTel()) && !PhoneUtil.isMobile(requestDTO.getUserTel())) {
            return Result.build(StatusCodeEnum.PARAM_INVALID).message("请输入正确的手机号");
        }

        // 查询用户关键信息是否存在
        Result<String> userInfoKeyExistsSearchResult = userInfoSearchFeignClient.userInfoKeyExistsSearch(requestDTO.getUserName(),
                requestDTO.getUserEmail(), requestDTO.getUserTel());
        if (Result.isNotSuccess(userInfoKeyExistsSearchResult)) {
            return userInfoKeyExistsSearchResult;
        }

        // 保存用户信息
        userInfoManager.insert(requestDTO);
        return Result.ok();
    }
}
