package com.doudoudrive.common.log.tracer;

import com.doudoudrive.common.log.tracer.filter.FeignTracerRequestFilter;
import com.doudoudrive.common.log.tracer.servlet.TracerServletMvcConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>日志追踪模块自动配置类</p>
 * <p>2022-11-17 22:44</p>
 *
 * @author Dan
 **/
@Configuration
public class TracerAutoConfiguration {

    /**
     * 日志追踪控制层mvc配置
     *
     * @return 日志追踪控制层mvc配置
     */
    @Bean
    public TracerServletMvcConfigurer tracerServletMvcConfigurer() {
        return new TracerServletMvcConfigurer();
    }

    /**
     * Feign请求拦截器
     *
     * @return Feign请求拦截器
     */
    @Bean
    public FeignTracerRequestFilter feignTracerRequestAutoConfig() {
        return new FeignTracerRequestFilter();
    }
}
