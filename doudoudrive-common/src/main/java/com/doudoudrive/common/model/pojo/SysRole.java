package com.doudoudrive.common.model.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>系统角色管理模块实体类</p>
 * <p>2022-04-06 12:33:29</p>
 *
 * @author Dan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysRole implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 自增长标识
     */
    private Long autoId;

    /**
     * 业务标识
     */
    private String businessId;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色描述
     */
    private String roleRemarks;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}