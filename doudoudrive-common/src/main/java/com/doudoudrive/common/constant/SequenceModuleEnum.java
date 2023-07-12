package com.doudoudrive.common.constant;

import lombok.Getter;

/**
 * <p>序列模块枚举信息</p>
 * <p>2022-03-06 22:02</p>
 *
 * @author Dan
 **/
@Getter
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
    SMS_SEND_RECORD("08"),

    /**
     * 用户属性记录
     */
    DISK_USER_ATTR("09"),

    /**
     * RocketMQ消费记录
     */
    ROCKETMQ_CONSUMER_RECORD("10"),

    /**
     * 用户文件模块
     */
    DISK_FILE("11"),

    /**
     * OSS文件对象存储
     */
    OSS_FILE("12"),

    /**
     * 文件临时操作记录模块
     */
    FILE_RECORD("13"),

    /**
     * 文件分享模块
     */
    FILE_SHARE("14"),

    /**
     * 文件分享记录详情模块
     */
    FILE_SHARE_DETAIL("15"),

    /**
     * 系统日志消息模块
     */
    SYS_LOGBACK("16"),

    /**
     * 外部系统回调记录模块
     */
    CALLBACK_RECORD("17");

    /**
     * 业务模块标识，固定为两位数字
     * 非特殊情况不支持修改，修改后对原有数据不兼容
     */
    private final String code;

    SequenceModuleEnum(String code) {
        this.code = code;
    }
}
