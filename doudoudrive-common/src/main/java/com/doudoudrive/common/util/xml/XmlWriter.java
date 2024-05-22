package com.doudoudrive.common.util.xml;

import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import lombok.SneakyThrows;
import org.w3c.dom.Node;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * <p>XML生成器</p>
 * <p>2024-04-23 15:26</p>
 *
 * @author Dan
 **/
public class XmlWriter {

    private final Source source;
    private Charset charset = StandardCharsets.UTF_8;
    private int indent;
    private boolean omitXmlDeclaration;
    /**
     * 构造
     *
     * @param source XML数据源
     */
    public XmlWriter(final Source source) {
        this.source = source;
    }

    /**
     * 构建XmlWriter
     *
     * @param node {@link Node} XML文档节点或文档本身
     * @return XmlWriter
     */
    public static XmlWriter of(final Node node) {
        return of(new DOMSource(node));
    }

    /**
     * 构建XmlWriter
     *
     * @param source XML数据源
     * @return XmlWriter
     */
    public static XmlWriter of(final Source source) {
        return new XmlWriter(source);
    }

    /**
     * 设置编码
     *
     * @param charset 编码，null跳过
     * @return this
     */
    public XmlWriter setCharset(final Charset charset) {
        if (null != charset) {
            this.charset = charset;
        }
        return this;
    }

    /**
     * 设置缩进
     *
     * @param indent 缩进
     * @return this
     */
    public XmlWriter setIndent(final int indent) {
        this.indent = indent;
        return this;
    }

    /**
     * 设置是否输出 xml Declaration
     *
     * @param omitXmlDeclaration 是否输出 xml Declaration
     * @return this
     */
    public XmlWriter setOmitXmlDeclaration(final boolean omitXmlDeclaration) {
        this.omitXmlDeclaration = omitXmlDeclaration;
        return this;
    }

    /**
     * 获得XML字符串
     *
     * @return XML字符串
     */
    @SneakyThrows
    public String getStr() {
        try (final StringWriter writer = new StringWriter()) {
            write(writer);
            return writer.toString();
        }
    }

    /**
     * 将XML文档写出
     *
     * @param file 目标
     */
    public void write(final File file) {
        write(new StreamResult(file));
    }

    /**
     * 将XML文档写出
     *
     * @param writer 目标
     */
    public void write(final Writer writer) {
        write(new StreamResult(writer));
    }

    /**
     * 将XML文档写出
     *
     * @param out 目标
     */
    public void write(final OutputStream out) {
        write(new StreamResult(out));
    }

    /**
     * 将XML文档写出<br>
     *
     * @param result 目标
     * @see <a href="https://stackoverflow.com/questions/139076/how-to-pretty-print-xml-from-java">xml-from-java</a>
     */
    @SneakyThrows
    public void write(final Result result) {
        final TransformerFactory factory = XmlExternalEntityAttackUtil.disableXxe(TransformerFactory.newInstance());
        final Transformer transFormer = factory.newTransformer();
        if (indent > NumberConstant.INTEGER_ZERO) {
            // 设置缩进
            transFormer.setOutputProperty(OutputKeys.INDENT, ConstantConfig.BooleanType.YES);
            transFormer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, ConstantConfig.BooleanType.YES);
            transFormer.setOutputProperty(XmlFeatures.INDENT_AMOUNT, String.valueOf(indent));
        }
        if (this.charset != null) {
            transFormer.setOutputProperty(OutputKeys.ENCODING, charset.name());
        }
        if (omitXmlDeclaration) {
            transFormer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, ConstantConfig.BooleanType.YES);
        }
        transFormer.transform(source, result);
    }
}
