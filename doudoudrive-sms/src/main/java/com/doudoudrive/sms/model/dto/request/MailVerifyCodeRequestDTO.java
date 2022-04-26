package com.doudoudrive.sms.model.dto.request;

import com.doudoudrive.common.constant.RegexConstant;
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
 * <p>校验邮箱验证码请求数据模型</p>
 * <p>2022-04-26 19:48</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MailVerifyCodeRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 收件人邮箱地址
     */
    @NotBlank(message = "请填写邮箱地址")
    @Size(max = 50, message = "邮箱格式错误")
    @Pattern(regexp = RegexConstant.EMAIL, message = "邮箱格式错误")
    private String email;

    @NotBlank(message = "请填写验证码")
    @Size(max = 4, message = "验证码无效")
    private String code;

}
