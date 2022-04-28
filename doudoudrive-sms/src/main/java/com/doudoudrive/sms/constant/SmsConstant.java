package com.doudoudrive.sms.constant;

/**
 * <p>通讯平台通用常量配置</p>
 * <p>2022-04-15 19:00</p>
 *
 * @author Dan
 **/
public interface SmsConstant {

    /**
     * freemarker模板名称字符串构造
     */
    String FREEMARKER_TEMPLATE_NAME = "%s.ftl";

    /**
     * freemarker子模板参数名
     */
    String SUB_TEMPLATE = "subTemplate";

    /**
     * freemarker父模板文件名
     */
    String MAIL_TEMPLATE = "mail-template.ftl";

    /**
     * 邮箱验证码数据模板相关常量
     */
    interface MailVerificationCode {
        /**
         * 邮箱验证码模板数据标识
         */
        String MAIL_VERIFICATION_CODE = "mail-verification-code";

        /**
         * 参数:验证码
         */
        String CODE = "code";
    }

    /**
     * 发送短信时使用的应用类型枚举
     */
    enum AppTypeEnum {
        /**
         * 使用阿里大鱼发送
         */
        ALIYUN("aliyun"),

        /**
         * 使用腾讯云发送
         */
        TENCENT_CLOUD("tencent");

        /**
         * 应用类型
         */
        public final String appType;

        AppTypeEnum(String appType) {
            this.appType = appType;
        }
    }
}
