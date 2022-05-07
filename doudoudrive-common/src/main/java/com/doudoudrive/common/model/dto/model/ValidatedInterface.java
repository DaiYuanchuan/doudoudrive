package com.doudoudrive.common.model.dto.model;

/**
 * <p>javax注解校验分组接口</p>
 * <p>2022-05-08 01:03</p>
 *
 * @author Dan
 **/
public interface ValidatedInterface {

    /**
     * 邮件类型-发送验证码时的校验分组
     */
    interface Mail {
    }

    /**
     * 短信类型-发送验证码时的校验分组
     */
    interface Sms {
    }

}
