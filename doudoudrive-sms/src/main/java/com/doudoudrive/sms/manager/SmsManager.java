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
     * 通讯平台通用发送接口
     *
     * @param model              自定义参数
     * @param smsSendRecordModel SMS发送记录的BO模型
     * @return 消息发送结果
     */
    SmsSendRecord send(Map<String, Object> model, SmsSendRecordModel smsSendRecordModel);

    /**
     * 通讯平台验证码信息发送，发送失败时会抛出异常
     *
     * @param securityCode       4位数随机安全码
     * @param smsSendRecordModel SMS发送记录的BO模型
     */
    void verificationCode(String securityCode, SmsSendRecordModel smsSendRecordModel);

}
