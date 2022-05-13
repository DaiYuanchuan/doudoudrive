package com.doudoudrive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * <p>兜兜网盘 - 用户信息服务</p>
 * <p>2022-03-01 17:55</p>
 *
 * @author Dan
 **/
@EnableWebMvc
@EnableDiscoveryClient
@SpringBootApplication
@EnableTransactionManagement
@EnableFeignClients(basePackages = {"com.doudoudrive.userinfo.client"})
public class UserInfoApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserInfoApplication.class, args);
    }
}
