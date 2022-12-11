package com.doudoudrive.common.config;

import com.doudoudrive.common.model.dto.model.WorkerUdpProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.ip.udp.UnicastSendingMessageHandler;

/**
 * <p>udp 服务集成客户端配置</p>
 * <p>2022-11-12 22:21</p>
 *
 * @author Dan
 **/
@Configuration
@EnableConfigurationProperties(WorkerUdpProperties.class)
public class UdpIntegrationClientConfig {

    /**
     * 获取配置类中worker模块的udp通信参数配置
     */
    private WorkerUdpProperties workerUdpProperties;

    @Autowired
    public void setWorkerUdpProperties(WorkerUdpProperties workerUdpProperties) {
        this.workerUdpProperties = workerUdpProperties;
    }

    /**
     * 单播发送udp消息处理配置
     *
     * @return {@link UnicastSendingMessageHandler}
     */
    @Bean
    public UnicastSendingMessageHandler sending() {
        return new UnicastSendingMessageHandler(workerUdpProperties.getServer(), workerUdpProperties.getPort(), workerUdpProperties.getLengthCheck());
    }
}
