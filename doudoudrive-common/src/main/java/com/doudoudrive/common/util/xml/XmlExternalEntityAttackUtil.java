package com.doudoudrive.common.util.xml;

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.XMLReader;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.validation.SchemaFactory;

/**
 * <p>XXE漏洞修复相关工具类</p>
 * <p>2024-04-23 15:30</p>
 *
 * @author Dan
 * @see <a href="https://blog.spoock.com/2018/10/23/java-xxe/">...</a>
 **/
public class XmlExternalEntityAttackUtil {

    /**
     * 关闭XXE，避免漏洞攻击<br>
     *
     * @param factory DocumentBuilderFactory
     * @return DocumentBuilderFactory
     */
    public static DocumentBuilderFactory disableXxe(final DocumentBuilderFactory factory) {
        try {
            // 这是主要的防御，如果不允许dtd (doctype)，几乎所有XML实体攻击都被阻止了
            factory.setFeature(XmlFeatures.DISALLOW_DOCTYPE_DECL, true);
            // 如果不能完全禁用dtd，那么至少执行以下操作
            factory.setFeature(XmlFeatures.EXTERNAL_GENERAL_ENTITIES, false);
            factory.setFeature(XmlFeatures.EXTERNAL_PARAMETER_ENTITIES, false);
            // 也禁用外部dtd
            factory.setFeature(XmlFeatures.LOAD_EXTERNAL_DTD, false);
        } catch (final ParserConfigurationException e) {
            // ignore
        }

        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);
        return factory;
    }

    /**
     * 关闭XEE避免漏洞攻击
     *
     * @param factory {@link SAXParserFactory}
     * @return {@link SAXParserFactory}
     */
    public static SAXParserFactory disableXxe(final SAXParserFactory factory) {
        try {
            factory.setFeature(XmlFeatures.DISALLOW_DOCTYPE_DECL, true);
            factory.setFeature(XmlFeatures.EXTERNAL_GENERAL_ENTITIES, false);
            factory.setFeature(XmlFeatures.EXTERNAL_PARAMETER_ENTITIES, false);
            factory.setFeature(XmlFeatures.LOAD_EXTERNAL_DTD, false);
        } catch (final Exception ignore) {
            // ignore
        }

        factory.setXIncludeAware(false);
        return factory;
    }

    /**
     * 关闭XEE避免漏洞攻击
     *
     * @param reader {@link XMLReader}
     * @return {@link XMLReader}
     */
    public static XMLReader disableXxe(final XMLReader reader) {
        try {
            reader.setFeature(XmlFeatures.DISALLOW_DOCTYPE_DECL, true);
            reader.setFeature(XmlFeatures.EXTERNAL_GENERAL_ENTITIES, false);
            reader.setFeature(XmlFeatures.EXTERNAL_PARAMETER_ENTITIES, false);
            reader.setFeature(XmlFeatures.LOAD_EXTERNAL_DTD, false);
        } catch (final Exception ignore) {
            // ignore
        }
        return reader;
    }

    /**
     * 关闭XEE避免漏洞攻击
     *
     * @param factory {@link TransformerFactory }
     * @return {@link TransformerFactory }
     */
    public static TransformerFactory disableXxe(final TransformerFactory factory) {
        try {
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        } catch (final TransformerConfigurationException e) {
            throw new IllegalArgumentException(e);
        }
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, StringUtils.EMPTY);
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, StringUtils.EMPTY);
        return factory;
    }

    /**
     * 关闭XEE避免漏洞攻击
     *
     * @param validator {@link javax.xml.validation.Validator }
     * @return {@link javax.xml.validation.Validator }
     */
    public static javax.xml.validation.Validator disableXxe(final javax.xml.validation.Validator validator) {
        try {
            validator.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, StringUtils.EMPTY);
            validator.setProperty(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, StringUtils.EMPTY);
        } catch (final Exception ignore) {
            // ignore
        }
        return validator;
    }

    /**
     * 关闭XEE避免漏洞攻击
     *
     * @param factory {@link SAXTransformerFactory}
     * @return {@link SAXTransformerFactory}
     */
    public static SAXTransformerFactory disableXxe(final SAXTransformerFactory factory) {
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, StringUtils.EMPTY);
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, StringUtils.EMPTY);
        return factory;
    }

    /**
     * 关闭XEE避免漏洞攻击
     *
     * @param factory {@link SchemaFactory}
     * @return {@link SchemaFactory}
     */
    public static SchemaFactory disableXxe(final SchemaFactory factory) {
        try {
            factory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, StringUtils.EMPTY);
            factory.setProperty(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, StringUtils.EMPTY);
        } catch (final Exception ignore) {
            // ignore
        }
        return factory;
    }
}
