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
    @NotBlank(message = "请输入用户名")
    @Size(min = 5, max = 25, message = "用户名称不符合要求，请重新设置")
    private String userName;

    /**
     * 用户邮箱
     */
    @NotBlank(message = "请输入邮箱地址")
    @Size(max = 50, message = "邮箱格式有误")
    @Pattern(regexp = RegexConstant.EMAIL, message = "邮箱格式有误")
    private String userEmail;

    /**
     * 验证码
     */
    @NotBlank(message = "请填写验证码")
    @Size(max = 4, message = "验证码无效")
    private String mailCode;

    /**
     * 用户手机号
     */
    @Size(max = 11, message = "手机号码格式不正确，请重新输入")
    private String userTel;

    /**
     * 手机验证码
     */
    @Size(max = 6, message = "验证码无效")
    private String telCode;

    /**
     * 用户明文密码
     */
    @NotBlank(message = "请输入用户密码")
    @Size(max = 30, message = "密码不符合要求，请重新设置")
    private String userPwd;

}
