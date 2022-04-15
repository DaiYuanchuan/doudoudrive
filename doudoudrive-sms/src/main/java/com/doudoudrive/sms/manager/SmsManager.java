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
     * @param model              自定义参数
     * @param smsSendRecordModel SMS发送记录的BO模型
     */
    void sendMail(Map<String, Object> model, SmsSendRecordModel smsSendRecordModel);

}
