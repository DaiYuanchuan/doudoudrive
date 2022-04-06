package com.doudoudrive.common.model.dto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * <p>系统用户角色数据模型</p>
 * <p>2022-04-06 16:19</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysUserRoleModel implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 当前角色所绑定的权限信息
     */
    private List<SysUserAuthModel> authInfo;

}
