package com.doudoudrive.common.constant;

/**
 * <p>字典数据常量信息</p>
 * <p>2022-04-15 11:26</p>
 *
 * @author Dan
 **/
public interface DictionaryConstant {

    /**
     * 邮件发送配置项
     */
    String MAIL_CONFIG = "mailConfig";

    /**
     * 短信、邮件最大吞吐量配置
     */
    String THROUGHPUT = "throughput";

    /**
     * 短信发送时的基本配置项
     */
    String SMS_CONFIG = "smsConfig";

    /**
     * 默认的用户头像
     */
    String DEFAULT_AVATAR = "defaultAvatar";

    /**
     * 全局对称加密密钥配置
     */
    String CIPHER = "cipher";

    /**
     * 七牛云对象存储相关配置
     */
    String QI_NIU_CONFIG = "qiNiuConfig";

    /**
     * 文件内容审核相关配置
     */
    String FILE_REVIEW_CONFIG = "fileReviewConfig";

    /**
     * 内部线程池相关配置
     */
    String THREAD_POOL_CONFIG = "threadPoolConfig";

    /**
     * MinIO 对象存储相关配置
     */
    String MINIO_CONFIG = "minioConfig";

}
