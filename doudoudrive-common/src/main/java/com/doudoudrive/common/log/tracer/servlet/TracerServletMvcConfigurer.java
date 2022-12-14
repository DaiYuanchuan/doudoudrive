package com.doudoudrive.common.log.tracer.servlet;

import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * <p>日志追踪控制层mvc配置</p>
 * <p>2022-11-17 18:15</p>
 *
 * @author Dan
 **/
@RequiredArgsConstructor
public class TracerServletMvcConfigurer implements WebMvcConfigurer {

    /**
     * 添加一个拦截器的配置
     *
     * @param registry 拦截器注册
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TracerServletInterceptor());
    }
}
