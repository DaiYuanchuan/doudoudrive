package com.doudoudrive.common.config;

import com.doudoudrive.common.model.dto.model.RocketMqProperties;
import com.doudoudrive.common.rocketmq.MethodInvoker;
import com.doudoudrive.common.rocketmq.RocketmqMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * <p>RocketMQ自动化配置</p>
 * <p>2022-03-10 22:53</p>
 *
 * @author Dan
 **/
@Component
@EnableConfigurationProperties(RocketMqProperties.class)
public class RocketMqAutoConfig {

    private RocketMqProperties rocketMqProperties;

    @Autowired
    public void setRocketMqProperties(RocketMqProperties rocketMqProperties) {
        this.rocketMqProperties = rocketMqProperties;
    }

    /**
     * RocketMQ消息监听器注入
     *
     * @return rocketMq消息侦听器的容器
     */
    @Bean
    @ConditionalOnMissingBean(RocketmqMessageListenerContainer.class)
    public RocketmqMessageListenerContainer rocketMessageListenerContainer() {
        RocketmqMessageListenerContainer container = new RocketmqMessageListenerContainer();
        container.setNameServer(rocketMqProperties.getNameServer());
        container.setConsumeThreadMax(rocketMqProperties.getConsumeThreadMax());
        container.setConsumeThreadMin(rocketMqProperties.getConsumeThreadMin());
        return container;
    }

    @Bean
    @ConditionalOnMissingBean(MethodInvoker.class)
    public MethodInvoker methodInvoker() {
        return new MethodInvoker();
    }
}
