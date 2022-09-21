package com.doudoudrive.task.config;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>xxl-job配置项</p>
 * <p>2022-08-30 18:16</p>
 *
 * @author Dan
 **/
@Slf4j
@Configuration
public class XxlJobConfig {

    /**
     * 获取配置类中所有的数据源相关配置
     * 获取到的第一个数据源为默认数据源配置
     *
     * @return 动态数据源相关配置参数属性集合
     */
    @Bean
    @ConfigurationProperties(prefix = "xxl.job")
    public ScheduledJobConfigProperties getScheduledJobConfigProperties() {
        return new ScheduledJobConfigProperties();
    }

    /**
     * 初始化xxl-job执行器
     *
     * @return 一个xxl-job执行器
     */
    @Bean
    public XxlJobSpringExecutor xxlJobExecutor() {
        ScheduledJobConfigProperties scheduledJobConfigProperties = getScheduledJobConfigProperties();
        // 初始化执行器
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(scheduledJobConfigProperties.getAdminAddresses());
        xxlJobSpringExecutor.setAppname(scheduledJobConfigProperties.getAppName());
        xxlJobSpringExecutor.setAddress(scheduledJobConfigProperties.getAddress());
        xxlJobSpringExecutor.setIp(scheduledJobConfigProperties.getIp());
        xxlJobSpringExecutor.setPort(scheduledJobConfigProperties.getPort());
        xxlJobSpringExecutor.setAccessToken(scheduledJobConfigProperties.getAccessToken());
        xxlJobSpringExecutor.setLogPath(scheduledJobConfigProperties.getLogPath());
        xxlJobSpringExecutor.setLogRetentionDays(scheduledJobConfigProperties.getLogRetentionDays());
        return xxlJobSpringExecutor;
    }
}
