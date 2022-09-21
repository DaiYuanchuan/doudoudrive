package com.doudoudrive.common.constant;

import com.doudoudrive.common.model.pojo.SysUserRole;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>系统角色编码相关枚举</p>
 * <p>2022-04-07 17:21</p>
 *
 * @author Dan
 **/
public enum RoleCodeEnum {

    /**
     * 管理员角色，拥有系统最大权限
     */
    ADMINISTRATOR("admin", "系统管理员", Boolean.FALSE),

    /**
     * 文件基础属性相关角色
     */
    FILE("file", "文件基础角色", Boolean.TRUE),

    /**
     * 文件分享角色
     */
    FILE_SHARE("share", "文件分享角色", Boolean.FALSE);

    /**
     * 角色编码
     */
    public final String roleCode;

    /**
     * 描述
     */
    public final String authName;

    /**
     * 是否为用户注册时使用的默认权限
     */
    public final Boolean isDefault;

    RoleCodeEnum(String roleCode, String authName, Boolean isDefault) {
        this.roleCode = roleCode;
        this.authName = authName;
        this.isDefault = isDefault;
    }

    /**
     * 构建初始化List
     *
     * @param userId    用户标识
     * @param isDefault 是否为用户注册时使用的默认权限
     * @return 初始化用户角色List集合
     */
    public static List<SysUserRole> builderList(String userId, Boolean isDefault) {
        final List<SysUserRole> sysUserRoleList = new ArrayList<>();
        for (RoleCodeEnum roleCodeEnum : values()) {
            if (roleCodeEnum.isDefault.equals(isDefault)) {
                sysUserRoleList.add(SysUserRole.builder()
                        .userId(userId)
                        .roleCode(roleCodeEnum.roleCode)
                        .remarks(roleCodeEnum.authName)
                        .build());
            }
        }
        return sysUserRoleList;
    }
}
