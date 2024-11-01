package com.doudoudrive.common.util.http;

import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.util.lang.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.util.Args;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>Http 响应管理</p>
 * <p>2024-04-21 13:53</p>
 *
 * @author Dan
 **/
@Slf4j
public class HttpResponse implements Closeable {

    /**
     * 默认缓冲区大小（4096）
     */
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
    /**
     * 响应的状态码
     */
    private final CloseableHttpResponse response;
    /**
     * 响应内容的消息体
     */
    private final HttpEntity entity;
    /**
     * 响应内容的字节数组，用于后面的读取
     */
    private byte[] body = new byte[0];

    /**
     * 构造
     *
     * @param response 响应
     */
    public HttpResponse(CloseableHttpResponse response) {
        this.response = Args.notNull(response, "Wrapped response");
        this.entity = response.getEntity();

        if (this.entity != null) {
            // 读取响应内容
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(DEFAULT_BUFFER_SIZE)) {
                this.entity.writeTo(outputStream);
                outputStream.flush();
                // 将响应内容转换为字节数组
                this.body = outputStream.toByteArray();
            } catch (Exception ignored) {
                // ignore
            }
        }
    }

    /**
     * 获取响应的状态码
     *
     * @return 状态码
     */
    public Integer getCode() {
        return response.getStatusLine().getStatusCode();
    }

    /**
     * 请求是否成功，判断依据为：状态码范围不是400~599内。
     *
     * @return 是否成功请求
     */
    public boolean isOk() {
        HttpStatus status = HttpStatus.resolve(this.getCode());
        return status != null && !status.is4xxClientError() && !status.is5xxServerError();
    }

    /**
     * 获取内容编码
     *
     * @return String 编码
     */
    public String contentEncoding() {
        return header(HttpHeaders.CONTENT_ENCODING);
    }

    /**
     * 获取内容长度，内容的字节数
     *
     * @return 内容的字节数，如果未知，则为负数
     * 如果内容长度已知但超过Long。MAX_VALUE，则返回一个负数。
     */
    public long contentLength() {
        return this.entity == null ? NumberConstant.INTEGER_MINUS_ONE : this.entity.getContentLength();
    }

    /**
     * 获取本次请求服务器返回的Cookie信息
     *
     * @return Cookie字符串
     */
    public String getCookieStr() {
        return header(HttpHeaders.SET_COOKIE);
    }

    // ---------------------------------------------------------------- Headers start

    /**
     * 获取响应头中的Content-Type
     *
     * @return Content-Type
     */
    public String getContentType() {
        if (this.entity == null) {
            return null;
        }

        // 获取响应头中的Content-Type
        Header header = this.entity.getContentType();
        return header == null ? null : header.getValue();
    }

    /**
     * 获取响应头中的Content-Encoding
     *
     * @return Content-Encoding
     */
    public String getContentEncoding() {
        if (this.entity == null) {
            return null;
        }

        // 获取响应头中的Content-Encoding
        Header header = this.entity.getContentEncoding();
        return header == null ? null : header.getValue();
    }

    /**
     * 获取响应头中的Content-Length
     *
     * @return Content-Length
     */
    public Long getContentLength() {
        if (this.entity == null) {
            return null;
        }

        // 获取响应头中的Content-Length
        long contentLength = this.entity.getContentLength();
        return contentLength == NumberConstant.INTEGER_MINUS_ONE ? null : contentLength;
    }

    /**
     * 根据name获取头信息<br>
     * 根据RFC2616规范，header的name不区分大小写
     *
     * @param name Header名
     * @return Header值
     */
    public String header(String name) {
        final List<String> values = headerList(name);
        if (CollectionUtil.isEmpty(values)) {
            return null;
        }
        return values.get(NumberConstant.INTEGER_ZERO);
    }

    /**
     * 根据name获取头信息列表
     *
     * @param name Header名
     * @return Header值
     */
    public List<String> headerList(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }

        // 获取指定名称的响应头
        Header[] headers = response.getHeaders(name.trim());
        if (headers == null) {
            return null;
        }

        // 将响应头转换为List
        return Arrays.stream(headers)
                .map(Header::getValue)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有头
     *
     * @return Map形式的所有头
     */
    public Map<String, List<String>> headers() {
        Header[] headers = response.getAllHeaders();
        if (headers == null) {
            return null;
        }

        return Arrays.stream(headers)
                .collect(Collectors.groupingBy(Header::getName, Collectors.mapping(Header::getValue, Collectors.toList())));
    }
    // ---------------------------------------------------------------- Http Response Header end

    // ---------------------------------------------------------------- Body start

    /**
     * 获得服务器响应流<br>
     * 流获取后处理完毕需关闭此类
     *
     * @return 响应流
     */
    public InputStream bodyStream() {
        return new ByteArrayInputStream(this.body);
    }

    /**
     * 获取响应流字节码<br>
     *
     * @return byte[]，如果无法读取返回null
     */
    public byte[] bodyBytes() {
        return this.body;
    }

    /**
     * 获取响应主体内容，响应内容将被转换为字符串<br>
     *
     * @return 响应主体内容
     */
    public String body() {
        return body(StandardCharsets.UTF_8);
    }

    /**
     * 获取响应主体内容，响应内容将被转换为字符串<br>
     *
     * @param charset 编码
     * @return 响应主体内容
     */
    public String body(Charset charset) {
        if (CollectionUtil.isEmpty(this.body)) {
            return StringUtils.EMPTY;
        }

        if (charset == null) {
            try {
                // 尝试从响应头中获取字符集
                ContentType contentType = ContentType.get(entity);
                if (contentType != null && contentType.getCharset() != null) {
                    charset = contentType.getCharset();
                }
            } catch (Exception ignored) {
                // ignore
            }
        }

        // 读取响应内容
        return new String(this.body, Optional.ofNullable(charset).orElse(StandardCharsets.UTF_8));
    }

    /**
     * 将响应内容写出到{@link OutputStream}<br>
     *
     * @param outStream 输出流
     * @return 写出的结果，true表示写出成功，false表示写出失败
     */
    public boolean writeBody(OutputStream outStream) {
        if (outStream == null) {
            return false;
        }

        try (WritableByteChannel channel = Channels.newChannel(outStream)) {
            // 包装字节数组到 ByteBuffer 中
            ByteBuffer buffer = ByteBuffer.wrap(this.body);
            // 将 ByteBuffer 中的数据写入到输出流中
            channel.write(buffer);
            outStream.flush();
        } catch (Exception e) {
            log.error("Write response body to output stream errMsg:{}", e.getMessage(), e);
            return false;
        }
        return true;
    }

    /**
     * 将响应内容写出到文件<br>
     *
     * @param targetFile 目标文件
     * @return 写出的结果，true表示写出成功，false表示写出失败
     */
    public boolean writeBody(File targetFile) {
        if (null == targetFile) {
            return false;
        }

        try (OutputStream out = Files.newOutputStream(targetFile.toPath());
             BufferedOutputStream buffer = new BufferedOutputStream(out)) {
            if (writeBody(buffer)) {
                return true;
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    // ---------------------------------------------------------------- Body end

    /**
     * 格式化输出响应内容
     *
     * @return 格式化后的响应内容
     */
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        // 响应的协议版本、状态码、状态描述
        str.append(response.getProtocolVersion()).append(StringUtils.SPACE)
                .append(this.getCode()).append(StringUtils.SPACE)
                .append(response.getStatusLine().getReasonPhrase()).append(ConstantConfig.SpecialSymbols.ENTER_LINUX);

        // 响应头
        this.headers().forEach((key, value) -> str.append(key).append(ConstantConfig.SpecialSymbols.ENGLISH_COLON)
                .append(StringUtils.SPACE).append(value).append(ConstantConfig.SpecialSymbols.ENTER_LINUX));

        // 响应内容
        str.append(ConstantConfig.SpecialSymbols.ENTER_LINUX).append(this.body());
        return str.toString();
    }

    @Override
    public void close() throws IOException {
        // 释放资源
        if (entity != null && entity.isStreaming()) {
            IOUtils.closeQuietly(entity.getContent());
        }
        IOUtils.closeQuietly(response);
        // 清空响应内容
        this.body = new byte[0];
    }
}
