package com.doudoudrive.log.consumer;

import com.doudoudrive.common.annotation.RocketmqListener;
import com.doudoudrive.common.annotation.RocketmqTagDistribution;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.model.pojo.LogLogin;
import com.doudoudrive.common.model.pojo.LogOp;
import com.doudoudrive.log.manager.LogOpManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>日志系统消费者服务</p>
 * <p>2022-03-08 17:09</p>
 *
 * @author Dan
 **/
@Component
@RocketmqListener(topic = ConstantConfig.Topic.LOG_RECORD, consumerGroup = ConstantConfig.ConsumerGroup.LOG)
public class LogServiceConsumer {

    private LogOpManager logOpManager;

    @Autowired
    public void setLogOpManager(LogOpManager logOpManager) {
        this.logOpManager = logOpManager;
    }

    /**
     * API操作日志消息的消费处理
     *
     * @param logOpInfo API操作日志信息
     */
    @RocketmqTagDistribution(messageClass = LogOp.class, tag = ConstantConfig.Tag.ACCESS_LOG_RECORD)
    public void logOpConsumer(LogOp logOpInfo) {
        // 保存日志信息
        logOpManager.insert(logOpInfo);
    }

    /**
     * 登录日志消息的消费处理
     *
     * @param logLogin 登录日志信息
     */
    @RocketmqTagDistribution(messageClass = LogLogin.class, tag = ConstantConfig.Tag.LOGIN_LOG_RECORD)
    public void logLoginConsumer(LogLogin logLogin) {
        // 保存日志信息
        logOpManager.insert(logLogin);
    }
}
