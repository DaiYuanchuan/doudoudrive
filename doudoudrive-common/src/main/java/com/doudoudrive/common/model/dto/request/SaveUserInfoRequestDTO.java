package com.doudoudrive.common.model.dto.request;

import com.doudoudrive.common.constant.RegexConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * <p>保存用户信息时的请求数据模型</p>
 * <p>2022-03-21 18:31</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveUserInfoRequestDTO {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 1, max = 50, message = "用户名称长度错误")
    private String userName;

    /**
     * 用户头像
     */
    @NotBlank(message = "用户头像不能为空")
    @Size(min = 1, max = 170, message = "url长度错误")
    @Pattern(regexp = RegexConstant.URL_HTTP, message = "url格式错误")
    private String userAvatar;

    /**
     * 用户邮箱
     */
    @NotBlank(message = "用户邮箱不能为空")
    @Size(min = 1, max = 50, message = "邮箱长度错误")
    @Pattern(regexp = RegexConstant.EMAIL, message = "邮箱格式错误")
    private String userEmail;

    /**
     * 用户手机号
     */
    @Size(max = 15, message = "手机号长度错误")
    private String userTel;

    /**
     * 用户明文密码
     */
    @NotBlank(message = "用户密码不能为空")
    @Size(min = 1, max = 30, message = "密码长度错误")
    private String userPwd;

}
