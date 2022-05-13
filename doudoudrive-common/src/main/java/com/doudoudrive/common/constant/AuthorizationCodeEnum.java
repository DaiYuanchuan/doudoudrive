package com.doudoudrive.common.constant;

/**
 * <p>系统基础权限编码相关枚举</p>
 * <p>2022-04-07 17:02</p>
 *
 * @author Dan
 **/
public enum AuthorizationCodeEnum {

    /**
     * 管理员权限，拥有系统最大权限
     */
    ADMINISTRATOR("admin", "系统管理员"),

    /**
     * 文件相关权限
     */
    FILE_UPLOAD("file:upload", "文件上传"),
    FILE_DELETE("file:delete", "文件删除"),
    FILE_UPDATE("file:update", "文件修改"),
    FILE_SELECT("file:select", "文件查询"),

    /**
     * 文件分享权限
     */
    FILE_SHARE("file:share", "文件分享");

    public final String authCode;
    public final String authName;

    AuthorizationCodeEnum(String authCode, String authName) {
        this.authCode = authCode;
        this.authName = authName;
    }
}
