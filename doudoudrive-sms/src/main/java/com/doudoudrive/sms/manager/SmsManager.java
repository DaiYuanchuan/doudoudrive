package com.doudoudrive.sms.manager;

import com.doudoudrive.common.model.dto.model.SmsSendRecordModel;

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
     * @param recipient          收件人邮箱
     * @param subject            邮件发送时的标题
     * @param dataId             需要发送的邮件内容模板id
     * @param model              自定义参数
     * @param smsSendRecordModel SMS发送记录的BO模型
     */
    void sendMail(String recipient, String subject, String dataId, Map<String, Object> model, SmsSendRecordModel smsSendRecordModel);

}
