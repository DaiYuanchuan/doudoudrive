package com.doudoudrive.common.constant;

/**
 * <p>系统基础权限编码相关常量配置</p>
 * <p>2022-04-07 17:02</p>
 *
 * @author Dan
 **/
public interface AuthorizationCodeConstant {

    /**
     * 系统管理员、管理员权限，拥有系统最大权限
     */
    String ADMINISTRATOR = "admin";

    /**
     * 文件上传
     */
    String FILE_UPLOAD = "file:upload";

    /**
     * 文件删除
     */
    String FILE_DELETE = "file:delete";

    /**
     * 文件修改
     */
    String FILE_UPDATE = "file:update";

    /**
     * 文件查询
     */
    String FILE_SELECT = "file:select";

    /**
     * 文件分享权限
     */
    String FILE_SHARE = "file:share";

}
