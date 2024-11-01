package com.doudoudrive.common.util.xml;

import javax.xml.parsers.SAXParserFactory;

/**
 * <p>{@link SAXParserFactory} 工具</p>
 * <p>2024-04-23 12:41</p>
 *
 * @author Dan
 **/
public class SaxParserFactoryUtil {

    /**
     * Sax读取器工厂缓存
     */
    private static volatile SAXParserFactory factory;

    /**
     * 获取全局{@link SAXParserFactory}<br>
     * <ul>
     *     <li>默认不验证</li>
     *     <li>默认打开命名空间支持</li>
     * </ul>
     *
     * @return {@link SAXParserFactory}
     */
    public static SAXParserFactory getFactory() {
        if (null == factory) {
            synchronized (SaxParserFactoryUtil.class) {
                if (null == factory) {
                    factory = createFactory(false, true);
                }
            }
        }
        return factory;
    }

    /**
     * 创建{@link SAXParserFactory}
     *
     * @param validating     是否验证
     * @param namespaceAware 是否打开命名空间支持
     * @return {@link SAXParserFactory}
     */
    public static SAXParserFactory createFactory(final boolean validating, final boolean namespaceAware) {
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(validating);
        factory.setNamespaceAware(namespaceAware);
        return XmlExternalEntityAttackUtil.disableXxe(factory);
    }
}
