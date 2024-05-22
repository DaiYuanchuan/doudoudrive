package com.doudoudrive.common.util.xml;

import com.doudoudrive.common.constant.ConstantConfig;

/**
 * <p>XXE安全相关参数</p>
 * <p>2024-04-23 12:56</p>
 *
 * @author Dan
 * @see <a href="https://blog.spoock.com/2018/10/23/java-xxe/">...</a>
 **/
public class XmlFeatures {

    /**
     * 禁用xml中的inline DOCTYPE 声明，即禁用DTD<br>
     * 不允许将外部实体包含在传入的 XML 文档中，从而防止XML实体注入（XML External Entities 攻击，利用能够在处理时动态构建文档的 XML 功能，注入外部实体）
     */
    public static final String DISALLOW_DOCTYPE_DECL = ConstantConfig.HttpRequest.HTTP_PROTOCOL + "://apache.org/xml/features/disallow-doctype-decl";
    /**
     * 忽略外部DTD
     */
    public static final String LOAD_EXTERNAL_DTD = ConstantConfig.HttpRequest.HTTP_PROTOCOL + "://apache.org/xml/features/nonvalidating/load-external-dtd";
    /**
     * 不包括外部一般实体
     */
    public static final String EXTERNAL_GENERAL_ENTITIES = ConstantConfig.HttpRequest.HTTP_PROTOCOL + "://xml.org/sax/features/external-general-entities";
    /**
     * 不包含外部参数实体或外部DTD子集。
     */
    public static final String EXTERNAL_PARAMETER_ENTITIES = ConstantConfig.HttpRequest.HTTP_PROTOCOL + "://xml.org/sax/features/external-parameter-entities";

    /**
     * 是否忽略注释
     */
    public static final String INDENT_AMOUNT = "{" + ConstantConfig.HttpRequest.HTTP_PROTOCOL + "://xml.apache.org/xslt}indent-amount";

}
