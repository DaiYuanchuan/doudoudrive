package com.doudoudrive.common.constant;

/**
 * <p>序列模块枚举信息</p>
 * <p>2022-03-06 22:02</p>
 *
 * @author Dan
 **/
public enum SequenceModuleEnum {

    /**
     * 数据字典
     */
    DICTIONARY("00"),

    /**
     * API操作日志模块
     */
    LOG_OP("01"),

    /**
     * 登录日志模块
     */
    LOG_LOGIN("02"),

    /**
     * 用户模块
     */
    DISK_USER("03"),

    /**
     * 系统权限
     */
    SYS_AUTH("04"),

    /**
     * 系统角色
     */
    SYS_ROLE("05"),

    /**
     * 系统角色与权限关联
     */
    SYS_ROLE_AUTH("06"),

    /**
     * 系统用户与角色关联
     */
    SYS_USER_ROLE("07"),

    /**
     * SMS发送记录
     */
    SMS_SEND_RECORD("08");

    /**
     * 业务模块标识，固定为两位数字
     * 非特殊情况不支持修改，修改后对原有数据不兼容
     */
    public final String code;

    SequenceModuleEnum(String code) {
        this.code = code;
    }
}
