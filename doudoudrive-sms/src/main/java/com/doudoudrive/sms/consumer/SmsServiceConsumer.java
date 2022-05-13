package com.doudoudrive.sms.consumer;

import com.doudoudrive.common.annotation.RocketmqListener;
import com.doudoudrive.common.annotation.RocketmqTagDistribution;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.model.dto.request.SendMailRequestDTO;
import com.doudoudrive.sms.config.SmsFactory;
import com.doudoudrive.sms.constant.SmsConstant;
import com.doudoudrive.sms.manager.SmsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>SMS系统消费者服务</p>
 * <p>2022-04-15 21:10</p>
 *
 * @author Dan
 **/
@Component
@RocketmqListener(topic = ConstantConfig.Topic.SMS_SERVICE, consumerGroup = ConstantConfig.ConsumerGroup.SMS)
public class SmsServiceConsumer {

    private SmsFactory smsFactory;

    @Autowired
    public void setSmsFactory(SmsFactory smsFactory) {
        this.smsFactory = smsFactory;
    }

    /**
     * 邮件发送消费者处理
     *
     * @param sendMailRequest 邮件发送请求数据模型
     */
    @RocketmqTagDistribution(messageClass = SendMailRequestDTO.class, tag = ConstantConfig.Tag.SEND_MAIL)
    public void sendMailConsumer(SendMailRequestDTO sendMailRequest) {
        // 获取邮件配置处理层接口
        SmsManager mailManager = smsFactory.getSmsManager(SmsConstant.AppType.MAIL);
        mailManager.send(sendMailRequest.getModel(), sendMailRequest.getSendRecordModel());
    }
}
