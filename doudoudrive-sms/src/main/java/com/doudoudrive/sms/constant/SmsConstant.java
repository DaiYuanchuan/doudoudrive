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

}
