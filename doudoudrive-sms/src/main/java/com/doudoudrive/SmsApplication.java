package com.doudoudrive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * <p>兜兜网盘 - 短信、邮件配置中心</p>
 * <p>2022-04-12 16:26</p>
 *
 * @author Dan
 **/
@EnableWebMvc
@EnableDiscoveryClient
@SpringBootApplication
@EnableTransactionManagement
public class SmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmsApplication.class, args);
    }

}
