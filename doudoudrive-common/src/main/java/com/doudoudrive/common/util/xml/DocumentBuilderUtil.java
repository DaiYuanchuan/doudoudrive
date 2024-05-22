package com.doudoudrive.common.util.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * <p>{@link DocumentBuilder} 工具类</p>
 * <p>2024-04-23 12:39</p>
 *
 * @author Dan
 **/
public class DocumentBuilderUtil {

    /**
     * 创建 DocumentBuilder
     *
     * @param namespaceAware 是否打开命名空间支持
     * @return DocumentBuilder
     */
    public static DocumentBuilder createDocumentBuilder(final boolean namespaceAware) throws ParserConfigurationException {
        return createDocumentBuilderFactory(namespaceAware).newDocumentBuilder();
    }

    /**
     * 创建{@link DocumentBuilderFactory}
     * <p>
     * 默认使用"com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl"<br>
     * </p>
     *
     * @param namespaceAware 是否打开命名空间支持
     * @return {@link DocumentBuilderFactory}
     */
    public static DocumentBuilderFactory createDocumentBuilderFactory(final boolean namespaceAware) {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        // 默认打开NamespaceAware，getElementsByTagNameNS可以使用命名空间
        factory.setNamespaceAware(namespaceAware);
        return XmlExternalEntityAttackUtil.disableXxe(factory);
    }
}
