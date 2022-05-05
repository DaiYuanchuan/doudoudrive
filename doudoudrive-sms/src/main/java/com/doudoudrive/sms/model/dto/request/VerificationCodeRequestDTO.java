package com.doudoudrive.sms.model.dto.request;

import com.doudoudrive.common.constant.RegexConstant;
import com.doudoudrive.sms.constant.SmsConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.util.Optional;

/**
 * <p>发送验证码时的请求数据模型</p>
 * <p>2022-04-25 18:11</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationCodeRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 收件人信息
     */
    @NotBlank(message = "请输入邮箱地址", groups = {SmsConstant.MailVerificationCode.class})
    @Size(max = 50, message = "邮箱格式有误", groups = {SmsConstant.MailVerificationCode.class})
    @Pattern(regexp = RegexConstant.EMAIL, message = "邮箱格式有误", groups = {SmsConstant.MailVerificationCode.class})
    @NotBlank(message = "请输入手机号码", groups = {SmsConstant.AliYunSmsTemplate.class})
    @Size(max = 11, message = "手机号码格式不正确，请重新输入", groups = {SmsConstant.AliYunSmsTemplate.class})
    @Pattern(regexp = RegexConstant.MOBILE, message = "手机号码格式不正确，请重新输入", groups = {SmsConstant.AliYunSmsTemplate.class})
    private String smsRecipient;

    /**
     * 当前操作的用户名，可以为null
     */
    private String username;

    /**
     * 如果exist值为true，则需要校验当前收件人是否存在，否则直接发送邮件、短信
     */
    private Boolean exist;

    /**
     * @return 如果exist值为Null，默认返回false
     */
    public Boolean getExist() {
        return Optional.ofNullable(exist).orElse(Boolean.FALSE);
    }
}
