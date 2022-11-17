package com.doudoudrive.auth.controller;

import com.doudoudrive.auth.manager.LoginManager;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.model.dto.response.UserLoginResponseDTO;
import com.doudoudrive.common.util.http.Result;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.Ordered;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>默认视图配置层</p>
 * <p>2022-04-04 21:58</p>
 *
 * @author Dan
 **/
@Slf4j
@Configuration
@RestController
@ImportResource(locations = {"classpath:spring-shiro.xml"})
public class DefaultViewController implements WebMvcConfigurer {

    private LoginManager loginManager;

    @Autowired
    public void setLoginManager(LoginManager loginManager) {
        this.loginManager = loginManager;
    }

    /**
     * 默认登录接口
     *
     * @param request  请求对象
     * @param response 响应对象
     * @return 返回一个用户登录模块响应数据模型
     */
    @SneakyThrows
    @RequestMapping(value = "/login", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    public Result<UserLoginResponseDTO> jumpLogin(HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding(ConstantConfig.HttpRequest.UTF8);
        response.setContentType(ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8);

        // 获取当前登录的用户信息
        UserLoginResponseDTO userLoginInfo = loginManager.getUserInfoToSession();
        if (userLoginInfo == null) {
            return Result.refuse();
        }
        return Result.ok(userLoginInfo);
    }

    /**
     * 添加一个默认页面
     *
     * @param registry 视图控制器注册器
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 默认页面
        registry.addViewController("/").setViewName("forward:/login");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }
}
