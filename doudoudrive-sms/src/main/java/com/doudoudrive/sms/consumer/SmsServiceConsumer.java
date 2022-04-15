package com.doudoudrive.sms.consumer;

import com.doudoudrive.common.annotation.RocketmqListener;
import com.doudoudrive.common.annotation.RocketmqTagDistribution;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.model.dto.request.SendMailRequestDTO;
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

    private SmsManager smsManager;

    @Autowired
    public void setSmsManager(SmsManager smsManager) {
        this.smsManager = smsManager;
    }

    /**
     * 邮件发送消费者处理
     *
     * @param sendMailRequest 邮件发送请求数据模型
     */
    @RocketmqTagDistribution(messageClass = SendMailRequestDTO.class, tag = ConstantConfig.Tag.SEND_MAIL)
    public void sendMailConsumer(SendMailRequestDTO sendMailRequest) {
        smsManager.sendMail(sendMailRequest.getModel(), sendMailRequest.getSendRecordModel());
    }
}
