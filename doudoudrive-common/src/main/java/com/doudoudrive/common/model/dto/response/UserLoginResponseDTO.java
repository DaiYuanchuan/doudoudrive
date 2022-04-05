package com.doudoudrive.common.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>用户登录模块响应数据模型</p>
 * <p>2022-04-04 21:45</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserLoginResponseDTO {

    /**
     * 登录的token，用于后续鉴权
     */
    private String token;

}
