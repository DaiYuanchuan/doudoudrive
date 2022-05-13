package com.doudoudrive.common.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * <p>用户登录模块请求数据模型</p>
 * <p>2022-04-04 21:38</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginRequestDTO {

    /**
     * 用户名
     */
    @NotBlank(message = "请输入用户名")
    @Size(min = 1, max = 45, message = "用户名或密码不正确")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "请输入密码")
    @Size(min = 1, max = 30, message = "用户名或密码不正确")
    private String password;

}
