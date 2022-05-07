package com.doudoudrive.userinfo.model.dto.request;

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
 * <p>重置用户密码时请求数据模型</p>
 * <p>2022-04-27 14:08</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "请输入邮箱或手机号")
    @Size(max = 45, message = "当前用户不存在")
    private String username;

    /**
     * 邮箱:1，短信:2
     */
    @NotBlank(message = "账号类型错误")
    @Pattern(regexp = "^[1-2_-]", message = "账号类型错误")
    private String type;

    @NotBlank(message = "请输入验证码")
    @Size(max = 6, message = "验证码无效")
    private String code;

    @NotBlank(message = "请输入用户密码")
    @Size(max = 30, message = "密码不符合要求，请重新设置")
    private String password;

}
