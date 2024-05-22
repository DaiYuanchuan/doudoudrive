package com.doudoudrive.common.util.xml;

import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * <p>XML SAX方式读取器</p>
 * <p>2024-04-23 14:52</p>
 *
 * @author Dan
 **/
public class XmlSaxReader {

    private final SAXParserFactory factory;
    private final InputSource source;

    /**
     * 构造
     *
     * @param factory {@link SAXParserFactory}
     * @param source  XML源，可以是文件、流、路径等
     */
    public XmlSaxReader(final SAXParserFactory factory, final InputSource source) {
        this.factory = factory;
        this.source = source;
    }

    /**
     * 创建XmlSaxReader，使用全局{@link SAXParserFactory}
     *
     * @param source XML源，可以是文件、流、路径等
     * @return XmlSaxReader
     */
    public static XmlSaxReader of(final InputSource source) {
        return of(SaxParserFactoryUtil.getFactory(), source);
    }

    /**
     * 创建XmlSaxReader
     *
     * @param factory {@link SAXParserFactory}
     * @param source  XML源，可以是文件、流、路径等
     * @return XmlSaxReader
     */
    public static XmlSaxReader of(final SAXParserFactory factory, final InputSource source) {
        return new XmlSaxReader(factory, source);
    }

    /**
     * 读取内容
     *
     * @param contentHandler XML流处理器，用于按照Element处理xml
     */
    public void read(final ContentHandler contentHandler) {
        final SAXParser parse;
        final XMLReader reader;
        try {
            parse = factory.newSAXParser();
            if (contentHandler instanceof DefaultHandler) {
                parse.parse(source, (DefaultHandler) contentHandler);
                return;
            }

            // 得到解读器
            reader = XmlExternalEntityAttackUtil.disableXxe(parse.getXMLReader());
            reader.setContentHandler(contentHandler);
            reader.parse(source);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
