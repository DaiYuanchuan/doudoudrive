package com.doudoudrive.common.model.dto.request;

import com.doudoudrive.common.constant.RegexConstant;
import com.doudoudrive.common.model.dto.model.ValidatedInterface;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;

/**
 * <p>校验验证码请求数据模型</p>
 * <p>2022-04-26 19:48</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyCodeRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 收件人信息
     */
    @NotBlank(message = "请输入邮箱地址", groups = {ValidatedInterface.Mail.class})
    @Size(max = 50, message = "邮箱格式有误", groups = {ValidatedInterface.Mail.class})
    @Pattern(regexp = RegexConstant.EMAIL, message = "邮箱格式有误", groups = {ValidatedInterface.Mail.class})
    @NotBlank(message = "请输入手机号码", groups = {ValidatedInterface.Sms.class})
    @Size(max = 11, message = "手机号码格式不正确，请重新输入", groups = {ValidatedInterface.Sms.class})
    @Pattern(regexp = RegexConstant.MOBILE, message = "手机号码格式不正确，请重新输入", groups = {ValidatedInterface.Sms.class})
    private String smsRecipient;

    /**
     * 验证码信息
     */
    @NotBlank(message = "请填写验证码", groups = {ValidatedInterface.Mail.class, ValidatedInterface.Sms.class})
    @Size(max = 4, message = "验证码无效", groups = {ValidatedInterface.Mail.class})
    @Size(max = 6, message = "验证码无效", groups = {ValidatedInterface.Sms.class})
    private String code;

}
