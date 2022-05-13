package com.doudoudrive.common.model.dto.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>密码加盐处理结果数据模型</p>
 * <p>2022-03-22 23:29</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SecretSaltingInfo {

    /**
     * 明文密码加密后的密文密码
     */
    private String password;

    /**
     * 密码加密的盐值
     */
    private String salt;

}
