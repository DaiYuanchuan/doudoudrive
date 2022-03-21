package com.doudoudrive.search.controller;

import cn.hutool.core.util.PhoneUtil;
import com.doudoudrive.common.annotation.OpLog;
import com.doudoudrive.common.global.StatusCodeEnum;
import com.doudoudrive.common.model.dto.request.DeleteElasticsearchUserInfoRequestDTO;
import com.doudoudrive.common.model.dto.request.SaveElasticsearchUserInfoRequestDTO;
import com.doudoudrive.common.model.dto.request.UpdateElasticsearchUserInfoRequestDTO;
import com.doudoudrive.common.model.dto.response.UsernameSearchResponseDTO;
import com.doudoudrive.common.util.http.Result;
import com.doudoudrive.search.manager.UserInfoSearchManager;
import com.doudoudrive.search.model.convert.UserInfoModelConvert;
import com.doudoudrive.search.model.elasticsearch.UserInfoDTO;
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
 * <p>用户信息搜索服务控制层实现</p>
 * <p>2022-03-20 22:37</p>
 *
 * @author Dan
 **/
@Slf4j
@Validated
@RestController
@RequestMapping(value = "/search/userinfo")
public class UserInfoSearchController {

    private UserInfoSearchManager userInfoSearchManager;

    private UserInfoModelConvert userInfoModelConvert;

    @Autowired
    public void setUserInfoSearchManager(UserInfoSearchManager userInfoSearchManager) {
        this.userInfoSearchManager = userInfoSearchManager;
    }

    @Autowired(required = false)
    public void setUserInfoModelConvert(UserInfoModelConvert userInfoModelConvert) {
        this.userInfoModelConvert = userInfoModelConvert;
    }

    @SneakyThrows
    @ResponseBody
    @OpLog(title = "保存用户信息", businessType = "ES用户信息查询服务")
    @PostMapping(value = "/save", produces = "application/json;charset=UTF-8")
    public Result<?> saveElasticsearchUserInfo(@RequestBody @Valid SaveElasticsearchUserInfoRequestDTO requestDTO,
                                               HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=UTF-8");
        // 手机号不为空，校验手机号格式是否正确
        if (StringUtils.isNotBlank(requestDTO.getUserTel()) && !PhoneUtil.isMobile(requestDTO.getUserTel())) {
            return Result.build(StatusCodeEnum.PARAM_INVALID).message("请输入正确的手机号");
        }
        // es中保存用户信息
        userInfoSearchManager.saveUserInfo(userInfoModelConvert.saveElasticsearchUserInfoRequestConvert(requestDTO));
        return Result.ok();
    }

    @SneakyThrows
    @ResponseBody
    @OpLog(title = "删除用户信息", businessType = "ES用户信息查询服务")
    @PostMapping(value = "/delete", produces = "application/json;charset=UTF-8")
    public Result<?> deleteElasticsearchUserInfo(@RequestBody @Valid DeleteElasticsearchUserInfoRequestDTO requestDTO,
                                                 HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=UTF-8");
        // 删除es中的数据
        userInfoSearchManager.deleteUserInfo(requestDTO.getBusinessId());
        return Result.ok();
    }

    @SneakyThrows
    @ResponseBody
    @OpLog(title = "更新用户信息", businessType = "ES用户信息查询服务")
    @PostMapping(value = "/update", produces = "application/json;charset=UTF-8")
    public Result<?> updateElasticsearchUserInfo(@RequestBody @Valid UpdateElasticsearchUserInfoRequestDTO requestDTO,
                                                 HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=UTF-8");

        // 需要更新的用户信息数据模型
        UserInfoDTO updatedUserInfo = userInfoModelConvert.updateElasticsearchUserInfoRequestConvert(requestDTO);
        // 构建es更新请求
        userInfoSearchManager.updateUserInfo(updatedUserInfo.getBusinessId(), updatedUserInfo);
        return Result.ok();
    }

    @SneakyThrows
    @OpLog(title = "用户名查询", businessType = "ES用户信息查询服务")
    @GetMapping(value = "/username-search", produces = "application/json;charset=UTF-8")
    public Result<UsernameSearchResponseDTO> usernameSearch(String username, HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=UTF-8");

        // 用户登录信息搜索
        UserInfoDTO userInfoDTO = userInfoSearchManager.userLoginInfoSearch(username);
        if (userInfoDTO == null) {
            return Result.build(StatusCodeEnum.USER_NO_EXIST);
        }
        return Result.ok(userInfoModelConvert.usernameSearchResponseConvert(userInfoDTO));
    }
}
