package com.doudoudrive.sms.manager;

import com.doudoudrive.common.model.dto.model.SmsSendRecordModel;
import com.doudoudrive.common.model.pojo.SmsSendRecord;

import java.util.Map;

/**
 * <p>通讯平台通用业务处理层接口</p>
 * <p>2022-04-12 16:45</p>
 *
 * @author Dan
 **/
public interface SmsManager {

    /**
     * 邮件发送
     *
     * @param model              自定义参数
     * @param smsSendRecordModel SMS发送记录的BO模型
     * @return 消息发送结果
     */
    SmsSendRecord sendMail(Map<String, Object> model, SmsSendRecordModel smsSendRecordModel);

    /**
     * 邮箱验证码信息发送
     *
     * @param email    需要发送到的收件人邮箱
     * @param username 当前操作的用户名，可以为null
     */
    void mailVerificationCode(String email, String username);

}
