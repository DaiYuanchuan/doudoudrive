package com.doudoudrive.commonservice.constant;

import cn.hutool.core.text.CharSequenceUtil;

import java.util.stream.Stream;

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
    DEFAULT("default", TransactionManagerConstant.DEFAULT_TRANSACTION_MANAGER),

    /**
     * 用户中心数据源
     */
    USERINFO("userInfo", TransactionManagerConstant.USERINFO_TRANSACTION_MANAGER),

    /**
     * 日志系统数据源
     */
    LOG("log", TransactionManagerConstant.LOG_TRANSACTION_MANAGER),

    /**
     * 系统静默配置中心数据源
     */
    CONFIG("config", TransactionManagerConstant.CONFIG_TRANSACTION_MANAGER),

    /**
     * 文件系统配置中心数据源
     */
    FILE("file", TransactionManagerConstant.FILE_TRANSACTION_MANAGER);

    /**
     * 数据源key
     */
    public final String dataSource;

    /**
     * 数据源对应的事务Bean名称
     */
    public final String transaction;

    DataSourceEnum(String dataSource, String transaction) {
        this.dataSource = dataSource;
        this.transaction = transaction;
    }

    /**
     * 根据数据源获取对应的事务名称，没有获取到时响应空字符串
     *
     * @param dataSource 数据源key
     * @return 事务Bean名称
     */
    public static String getTransactionValue(String dataSource) {
        return Stream.of(DataSourceEnum.values())
                .filter(anEnum -> anEnum.dataSource.equals(dataSource))
                .map(anEnum -> anEnum.transaction).findFirst().orElse(CharSequenceUtil.EMPTY);
    }
}
