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
import java.util.Optional;

/**
 * <p>发送邮箱验证码请求数据模型</p>
 * <p>2022-04-25 18:11</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MailVerificationCodeRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 收件人邮箱地址
     */
    @NotBlank(message = "请填写邮箱地址")
    @Size(max = 50, message = "邮箱格式错误")
    @Pattern(regexp = RegexConstant.EMAIL, message = "邮箱格式错误")
    private String email;

    /**
     * 当前操作的用户名，可以为null
     */
    private String username;

    /**
     * 如果exist值为true，则需要校验当前邮箱是否存在，否则直接发送邮件
     */
    private Boolean exist;

    /**
     * @return 如果exist值为Null，默认返回false
     */
    public Boolean getExist() {
        return Optional.ofNullable(exist).orElse(Boolean.FALSE);
    }
}
