package com.doudoudrive.common.model.dto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class SysUserAuthModel {

    /**
     * 授权编码
     */
    private String authCode;

}
