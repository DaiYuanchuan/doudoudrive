package com.doudoudrive.common.model.dto.model.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>系统用户权限数据模型</p>
 * <p>2022-04-06 16:19</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysUserAuthModel implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 授权编码
     */
    private String authCode;

}
