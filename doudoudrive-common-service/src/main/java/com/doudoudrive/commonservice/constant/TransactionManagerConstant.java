package com.doudoudrive.commonservice.constant;

/**
 * <p>事务管理器相关常量</p>
 * <p>2022-04-04 17:01</p>
 *
 * @author Dan
 **/
public class TransactionManagerConstant {

    /**
     * 默认事务管理器
     */
    public static final String DEFAULT_TRANSACTION_MANAGER = "transactionManager";

    /**
     * 用户中心事务管理器
     */
    public static final String USERINFO_TRANSACTION_MANAGER = "userInfoTransactionManager";

    /**
     * 日志系统的事务管理器
     */
    public static final String LOG_TRANSACTION_MANAGER = "logTransactionManager";

    /**
     * 系统静默配置中心的事务管理器
     */
    public static final String CONFIG_TRANSACTION_MANAGER = "configTransactionManager";

    /**
     * 文件系统配置中心的事务管理器
     */
    public static final String FILE_TRANSACTION_MANAGER = "fileTransactionManager";

    /**
     * 文件分享系统配置中心的事务管理器
     */
    public static final String FILE_SHARE_TRANSACTION_MANAGER = "fileShareTransactionManager";

}
