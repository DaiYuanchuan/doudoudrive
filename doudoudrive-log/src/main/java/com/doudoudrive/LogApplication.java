package com.doudoudrive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * <p>兜兜网盘 - 日志信息服务</p>
 * <p>2022-03-06 12:43</p>
 *
 * @author Dan
 **/
@EnableWebMvc
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
@EnableTransactionManagement
public class LogApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogApplication.class, args);
    }
}
