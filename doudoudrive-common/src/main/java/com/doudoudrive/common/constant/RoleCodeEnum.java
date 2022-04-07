package com.doudoudrive.common.constant;

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
    ADMINISTRATOR("admin", "系统管理员"),

    /**
     * 文件基础属性相关角色
     */
    FILE("file", "文件基础角色"),

    /**
     * 文件分享角色
     */
    FILE_SHARE("share", "文件分享角色");

    public final String roleCode;
    public final String authName;

    RoleCodeEnum(String roleCode, String authName) {
        this.roleCode = roleCode;
        this.authName = authName;
    }
}
