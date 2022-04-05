package com.doudoudrive.auth.controller;

import com.doudoudrive.auth.model.dto.MockToken;
import com.doudoudrive.common.annotation.OpLog;
import com.doudoudrive.common.model.dto.request.UserLoginRequestDTO;
import com.doudoudrive.common.model.dto.response.UserLoginResponseDTO;
import com.doudoudrive.common.util.http.Result;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * <p>登陆模块控制层</p>
 * <p>2022-04-04 21:35</p>
 *
 * @author Dan
 **/
@Slf4j
@Validated
@RestController
@RequestMapping(value = "/oauth")
public class LoginController {

    @SneakyThrows
    @OpLog(title = "密码登录", businessType = "用户登录")
    @PostMapping(value = "/login", produces = "application/json;charset=UTF-8")
    public Result<UserLoginResponseDTO> login(@RequestBody @Valid UserLoginRequestDTO requestDTO,
                                              HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=UTF-8");

        if (SecurityUtils.getSubject().isAuthenticated()) {
            log.info("重复调用登陆模块");
        }

        try {
            SecurityUtils.getSubject().login(new MockToken(requestDTO.getUsername()));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.refuse();
        }

        return Result.ok();
    }
}
