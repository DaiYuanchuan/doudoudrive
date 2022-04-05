package com.doudoudrive.auth.controller;

import com.doudoudrive.common.util.http.Result;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.Ordered;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

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
public class DefaultViewController extends WebMvcConfigurationSupport {

    @SneakyThrows
    @RequestMapping(value = "/login", produces = "application/json;charset=UTF-8")
    public Result<String> jumpLogin(HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=UTF-8");
        return Result.refuse();
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 默认页面
        registry.addViewController("/").setViewName("forward:/login");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
        super.addViewControllers(registry);
    }


}
