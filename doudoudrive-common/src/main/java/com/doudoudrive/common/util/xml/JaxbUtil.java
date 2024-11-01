package com.doudoudrive.common.util.xml;

import com.doudoudrive.common.util.lang.ConvertUtil;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * <p>JAXB（Java Architecture for XML Binding），根据XML Schema产生Java对象，即实现xml和Bean互转</p>
 * 相关介绍：
 * <ul>
 *   <li><a href="https://www.cnblogs.com/yanghaolie/p/11110991.html">https://www.cnblogs.com/yanghaolie/p/11110991.html</a></li>
 *   <li><a href="https://my.oschina.net/u/4266515/blog/3330113">https://my.oschina.net/u/4266515/blog/3330113</a></li>
 * </ul>
 * <p>2024-04-23 22:29</p>
 *
 * @author Dan
 **/
public class JaxbUtil {

    /**
     * JavaBean转换成xml
     * <p>
     * bean上面用的常用注解
     *
     * @param bean Bean对象
     * @return 输出的XML字符串
     */
    public static String beanToXml(final Object bean) {
        return beanToXml(bean, StandardCharsets.UTF_8, false);
    }

    /**
     * JavaBean转换成xml
     *
     * @param bean    Bean对象
     * @param charset 编码 eg: utf-8
     * @param format  是否格式化输出eg: true
     * @return 输出的XML字符串
     */
    public static String beanToXml(final Object bean, final Charset charset, final boolean format) {
        try (final StringWriter writer = new StringWriter()) {
            final JAXBContext context = JAXBContext.newInstance(bean.getClass());
            final Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, format);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, charset.name());
            marshaller.marshal(bean, writer);
            return writer.toString();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * xml转换成JavaBean
     *
     * @param <T>               Bean类型
     * @param xml               XML字符串
     * @param bean              Bean类型
     * @param isIgnoreNamespace 是否忽略命名空间
     * @return bean
     */
    public static <T> T xmlToBean(final String xml, final Class<T> bean, boolean isIgnoreNamespace) {
        if (StringUtils.isBlank(xml)) {
            return null;
        }
        return xmlToBean(new StringReader(xml), bean, isIgnoreNamespace);
    }

    /**
     * XML文件转Bean
     *
     * @param file              文件
     * @param charset           编码
     * @param bean              Bean类
     * @param <T>               Bean类型
     * @param isIgnoreNamespace 是否忽略命名空间
     * @return Bean
     */
    public static <T> T xmlToBean(final File file, final Charset charset, final Class<T> bean, boolean isIgnoreNamespace) {
        try (FileInputStream inputStream = new FileInputStream(file);
             BufferedInputStream buffer = new BufferedInputStream(inputStream);
             InputStreamReader reader = new InputStreamReader(buffer, charset);
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            return xmlToBean(bufferedReader, bean, isIgnoreNamespace);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 从{@link Reader}中读取XML字符串，并转换为Bean，忽略命名空间
     *
     * @param reader            {@link Reader}
     * @param bean              Bean类
     * @param isIgnoreNamespace 是否忽略命名空间
     * @param <T>               Bean类型
     * @return Bean
     */
    public static <T> T xmlToBean(final Reader reader, final Class<T> bean, boolean isIgnoreNamespace) {
        try {
            final JAXBContext context = JAXBContext.newInstance(bean);
            final Unmarshaller unmarshaller = context.createUnmarshaller();
            if (isIgnoreNamespace) {
                // 忽略命名空间
                final SAXParserFactory sax = SAXParserFactory.newInstance();
                sax.setNamespaceAware(false);
                final XMLReader xmlReader = sax.newSAXParser().getXMLReader();
                Source source = new SAXSource(xmlReader, new InputSource(reader));
                return ConvertUtil.convert(unmarshaller.unmarshal(source));
            }
            return ConvertUtil.convert(unmarshaller.unmarshal(reader));
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }
}
