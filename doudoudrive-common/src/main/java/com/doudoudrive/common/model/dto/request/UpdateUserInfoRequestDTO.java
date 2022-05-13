package com.doudoudrive.common.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

/**
 * <p>修改用户信息时的请求数据模型</p>
 * <p>2022-05-12 22:32</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserInfoRequestDTO {

    /**
     * 用户头像
     */
    @Size(max = 170, message = "URL过长")
    private String userAvatar;

    /**
     * 用户邮箱
     */
    @Size(max = 50, message = "邮箱过长")
    private String userEmail;

    /**
     * 用户邮箱验证码(修改邮箱时必填)
     */
    @Size(max = 4, message = "验证码无效")
    private String mailCode;

    /**
     * 用户手机号
     */
    @Size(max = 11, message = "手机号码格式不正确，请重新输入")
    private String userTel;

    /**
     * 用户手机验证码(修改手机号时必填)
     */
    @Size(max = 6, message = "验证码无效")
    private String smsCode;

    /**
     * 用户明文密码，留空以避免修改
     */
    @Size(max = 30, message = "密码不符合要求，请重新设置")
    private String password;

    /**
     * 原始密码，修改密码时必填
     */
    @Size(max = 30, message = "原始密码输入错误，请重新输入")
    private String oldPassword;

}
