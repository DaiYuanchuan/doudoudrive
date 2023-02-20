package com.doudoudrive.common.constant;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * <p>系统角色编码相关枚举</p>
 * <p>2022-04-07 17:21</p>
 *
 * @author Dan
 **/
@Getter
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
    private final String roleCode;

    /**
     * 描述
     */
    private final String authName;

    /**
     * 是否为用户注册时使用的默认权限
     */
    private final Boolean isDefault;

    RoleCodeEnum(String roleCode, String authName, Boolean isDefault) {
        this.roleCode = roleCode;
        this.authName = authName;
        this.isDefault = isDefault;
    }

    /**
     * 构建初始化List
     *
     * @param isDefault 是否为用户注册时使用的默认权限
     * @return 初始化用户角色List集合
     */
    public static List<RoleCodeEnum> builderList(Boolean isDefault) {
        return Arrays.stream(values())
                .filter(roleCodeEnum -> roleCodeEnum.isDefault.equals(isDefault))
                .toList();
    }
}
