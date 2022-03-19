package com.doudoudrive.commonservice.constant;

/**
 * <p>数据源枚举，记录项目所有数据源</p>
 * <p>2022-03-19 15:35</p>
 *
 * @author Dan
 **/
public enum DataSourceEnum {

    /**
     * 默认数据源
     * 默认取值为当前配置文件中的第一个数据源配置
     */
    DEFAULT("default"),

    /**
     * 用户中心数据源
     */
    USERINFO("userInfo"),

    /**
     * 日志系统数据源
     */
    LOG("log");

    /**
     * 数据源key
     */
    public final String dataSource;

    DataSourceEnum(String dataSource) {
        this.dataSource = dataSource;
    }
}
