package com.doudoudrive.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Collections;

/**
 * <p>全局跨域配置</p>
 * <p>2020-09-13 14:49</p>
 *
 * @author Dan
 **/
@Configuration
public class CorsConfig {

    /**
     * 解决ajax请求跨域问题
     *
     * @return CORS 过滤器
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // 添加一个请求头
        corsConfiguration.addAllowedHeader("*");
        // 设置允许跨域请求的域名
        corsConfiguration.addAllowedOrigin("*");
        // 设置允许的方法
        corsConfiguration.setAllowedMethods(Collections.singletonList("OPTIONS,HEAD,GET,PUT,POST,DELETE,PATCH"));
        // 当Credentials为true时，Origin不能为*，需要为具体的IP、域名地址 [如果接口不带Cookie,IP无需设成具体IP]
        corsConfiguration.setAllowCredentials(false);
        // 跨域允许时间
        corsConfiguration.setMaxAge(3600L);

        // 设置需要添加跨域请求的路径
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(source);
    }
}
