package com.doudoudrive.common.util.http;

import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.util.lang.CollectionUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MimeTypeUtils;

import java.io.IOException;
import java.net.HttpCookie;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>Http 请求管理</p>
 * <p>2024-04-21 13:52</p>
 *
 * @author Dan
 **/
public class HttpRequest {

    /**
     * 请求内容构建器
     */
    private RequestBuilder requestBuilder;
    /**
     * Http连接超时配置
     */
    private HttpConnection.HttpConnectConfig httpConnectConfig;

    /**
     * 构造器
     *
     * @param requestBuilder 请求构建器
     */
    private HttpRequest(RequestBuilder requestBuilder) {
        this.requestBuilder = requestBuilder;
        this.httpConnectConfig = new HttpConnection.HttpConnectConfig();
    }

    /**
     * POST请求
     *
     * @param url URL
     * @return HttpRequest
     */
    public static HttpRequest post(String url) {
        return getInstance(RequestBuilder.create(HttpPost.METHOD_NAME)
                .setUri(url)
                .setCharset(StandardCharsets.UTF_8));
    }

    /**
     * GET请求
     *
     * @param url URL
     * @return HttpRequest
     */
    public static HttpRequest get(String url) {
        return getInstance(RequestBuilder.create(HttpGet.METHOD_NAME)
                .setUri(url)
                .setCharset(StandardCharsets.UTF_8));
    }

    /**
     * HEAD请求
     *
     * @param url URL
     * @return HttpRequest
     */
    public static HttpRequest head(String url) {
        return getInstance(RequestBuilder.create(HttpHead.METHOD_NAME)
                .setUri(url)
                .setCharset(StandardCharsets.UTF_8));
    }

    /**
     * OPTIONS请求
     *
     * @param url URL
     * @return HttpRequest
     */
    public static HttpRequest options(String url) {
        return getInstance(RequestBuilder.create(HttpOptions.METHOD_NAME)
                .setUri(url)
                .setCharset(StandardCharsets.UTF_8));
    }

    /**
     * PUT请求
     *
     * @param url URL
     * @return HttpRequest
     */
    public static HttpRequest put(String url) {
        return getInstance(RequestBuilder.create(HttpPut.METHOD_NAME)
                .setUri(url)
                .setCharset(StandardCharsets.UTF_8));
    }

    /**
     * PATCH请求
     *
     * @param url URL
     * @return HttpRequest
     */
    public static HttpRequest patch(String url) {
        return getInstance(RequestBuilder.create(HttpPatch.METHOD_NAME)
                .setUri(url)
                .setCharset(StandardCharsets.UTF_8));
    }

    // ---------------------------------------------------------------- static Http Method end

    /**
     * DELETE请求
     *
     * @param url URL
     * @return HttpRequest
     */
    public static HttpRequest delete(String url) {
        return getInstance(RequestBuilder.create(HttpDelete.METHOD_NAME)
                .setUri(url)
                .setCharset(StandardCharsets.UTF_8));
    }

    /**
     * TRACE请求
     *
     * @param url URL
     * @return HttpRequest
     */
    public static HttpRequest trace(String url) {
        return getInstance(RequestBuilder.create(HttpTrace.METHOD_NAME)
                .setUri(url)
                .setCharset(StandardCharsets.UTF_8));
    }

    /**
     * 根据请求构建器创建一个请求
     *
     * @param requestBuilder 请求构建器
     * @return 请求
     */
    public static HttpRequest getInstance(RequestBuilder requestBuilder) {
        return new HttpRequest(requestBuilder);
    }

    /**
     * 一个简单的分析内容类型的方法
     *
     * @param content 内容
     * @return 内容类型
     */
    private static String analyzeContentType(String content) {
        // 检查是否是JSON格式
        if (content.startsWith(ConstantConfig.SpecialSymbols.LEFT_BRACE) && content.endsWith(ConstantConfig.SpecialSymbols.RIGHT_BRACE)) {
            return ConstantConfig.HttpRequest.CONTENT_TYPE_JSON;
        }

        final String leftBound = "<";
        final String rightBound = ">";

        // 检查是否是XML格式
        if (content.contains("<?")
                || content.contains("<!DOCTYPE")
                || content.startsWith(leftBound)
                && content.endsWith(rightBound)) {
            return MimeTypeUtils.APPLICATION_XML_VALUE;
        }

        // 检查是否是HTML格式
        if (content.contains("<html")
                || content.contains("<body")
                || content.contains("<head")) {
            return MimeTypeUtils.TEXT_HTML_VALUE;
        }

        // 检查是否是纯文本格式
        final String textRegex = "^\\p{Print}+$";
        if (content.matches(textRegex)) {
            return MimeTypeUtils.TEXT_PLAIN_VALUE;
        }

        // 其他情况，默认为null
        return null;
    }

    /**
     * 设置连接超时时间
     *
     * @param timeout 超时时间
     * @return HttpRequest
     */
    public HttpRequest setConnectTimeout(Integer timeout) {
        this.httpConnectConfig.setConnectTimeout(timeout);
        return this;
    }

    // ---------------------------------------------------------------- Http Request Header start

    /**
     * 设置获取请求连接超时时间
     *
     * @param timeout 超时时间
     * @return HttpRequest
     */
    public HttpRequest setConnectionRequestTimeout(Integer timeout) {
        this.httpConnectConfig.setConnectionRequestTimeout(timeout);
        return this;
    }

    /**
     * 设置响应超时时间
     *
     * @param timeout 超时时间
     * @return HttpRequest
     */
    public HttpRequest setSocketTimeout(Integer timeout) {
        this.httpConnectConfig.setSocketTimeout(timeout);
        return this;
    }

    /**
     * 设置contentType
     *
     * @param contentType contentType
     * @return HttpRequest
     */
    public HttpRequest setContentType(String contentType) {
        return setHeader(HttpHeaders.CONTENT_TYPE, contentType, false);
    }

    /**
     * 设置是否为长连接
     *
     * @param isKeepAlive 是否长连接
     * @return HttpRequest
     */
    public HttpRequest setKeepAlive(Boolean isKeepAlive) {
        String keepAlive = isKeepAlive ? org.apache.http.protocol.HTTP.CONN_KEEP_ALIVE : org.apache.http.protocol.HTTP.CONN_CLOSE;
        return setHeader(HttpHeaders.CONNECTION, keepAlive, false);
    }

    /**
     * 设置内容长度
     *
     * @param value 长度
     * @return HttpRequest
     */
    public HttpRequest setContentLength(String value) {
        return setHeader(HttpHeaders.CONTENT_LENGTH, value, false);
    }

    /**
     * 设置一个请求头，覆盖模式
     *
     * @param name       请求头名
     * @param value      请求头值
     * @param isOverride 是否覆盖已有的请求头
     * @return HttpRequest
     */
    public HttpRequest setHeader(String name, String value, boolean isOverride) {
        if (isOverride) {
            requestBuilder.removeHeaders(name);
        }
        requestBuilder.addHeader(name, value == null ? StringUtils.EMPTY : value);
        return this;
    }

    /**
     * 设置请求头，覆盖模式
     *
     * @param header 请求头
     * @return HttpRequest
     */
    public HttpRequest setHeader(Map<String, String> header) {
        return this.setHeader(header, true);
    }

    /**
     * 设置请求头，覆盖模式
     *
     * @param header     请求头
     * @param isOverride 是否覆盖已有的请求头
     * @return HttpRequest
     */
    public HttpRequest setHeader(Map<String, String> header, boolean isOverride) {
        if (CollectionUtil.isEmpty(header)) {
            return this;
        }

        for (Map.Entry<String, String> entry : header.entrySet()) {
            setHeader(entry.getKey(), entry.getValue(), isOverride);
        }
        return this;
    }

    /**
     * 设置请求头
     *
     * @param headers 请求头
     * @return HttpRequest
     */
    public HttpRequest header(Map<String, List<String>> headers) {
        return this.header(headers, true);
    }

    /**
     * 设置请求头
     *
     * @param headers    请求头
     * @param isOverride 是否覆盖已有的请求头
     * @return HttpRequest
     */
    public HttpRequest header(Map<String, List<String>> headers, boolean isOverride) {
        if (CollectionUtil.isEmpty(headers)) {
            return this;
        }

        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            for (String value : entry.getValue()) {
                setHeader(entry.getKey(), value, isOverride);
            }
        }
        return this;
    }

    /**
     * 移除请求头
     *
     * @param name 请求头名
     * @return HttpRequest
     */
    public HttpRequest removeHeader(String name) {
        requestBuilder.removeHeaders(name);
        return this;
    }

    /**
     * 设置http协议版本
     *
     * @param protocol 协议的名称，例如HTTP
     * @param major    主版本号，例如HTTP/1.1中的1
     * @param minor    次版本号，例如HTTP/1.1中的1
     * @return HttpRequest
     */
    public HttpRequest httpVersion(final String protocol, final int major, final int minor) {
        requestBuilder.setVersion(new ProtocolVersion(protocol, major, minor));
        return this;
    }

    /**
     * 设置请求字符集
     *
     * @param charset 字符集
     * @return HttpRequest
     */
    public HttpRequest charset(Charset charset) {
        requestBuilder.setCharset(charset);
        return this;
    }

    /**
     * 设置Cookie信息
     *
     * @param cookies Cookie值数组，如果为{@code null}则设置无效
     * @return HttpRequest
     */
    public HttpRequest cookie(Collection<HttpCookie> cookies) {
        return cookie(CollectionUtil.isEmpty(cookies) ? null : cookies.toArray(new HttpCookie[0]));
    }

    // ---------------------------------------------------------------- Http Request Header end

    /**
     * 设置Cookie
     *
     * @param cookies Cookie值数组，如果为{@code null}则设置无效
     * @return HttpRequest
     */
    public HttpRequest cookie(HttpCookie... cookies) {
        if (CollectionUtil.isEmpty(cookies)) {
            // 清空cookie
            cookie(StringUtils.EMPTY);
        }

        return cookie(Arrays.stream(cookies)
                .map(cookie -> cookie.getName() + ConstantConfig.SpecialSymbols.EQUALS + cookie.getValue())
                .collect(Collectors.joining(ConstantConfig.SpecialSymbols.SEMICOLON + StringUtils.SPACE)));
    }

    /**
     * 设置Cookie信息
     *
     * @param cookie Cookie值，如果为{@code null}则设置无效
     * @return HttpRequest
     */
    public HttpRequest cookie(String cookie) {
        setHeader(HttpHeaders.COOKIE, cookie, true);
        return this;
    }

    /**
     * 设置表单数据<br>
     *
     * @param name  名
     * @param value 值
     * @return HttpRequest
     */
    public HttpRequest form(String name, String value) {
        if (StringUtils.isBlank(name) || value == null) {
            // 忽略非法的form表单项内容
            return this;
        }

        this.requestBuilder.addParameter(name, value);
        return this;
    }
    // ---------------------------------------------------------------- Form end

    // ---------------------------------------------------------------- Body start

    /**
     * 设置表单数据
     *
     * @param name       名
     * @param value      值
     * @param parameters 参数对，奇数为名，偶数为值
     * @return this
     */
    public HttpRequest form(String name, String value, String... parameters) {
        form(name, value);

        for (int i = 0; i < parameters.length; i += 2) {
            form(parameters[i], parameters[i + 1]);
        }
        return this;
    }

    /**
     * 设置map类型表单数据
     *
     * @param formMap 表单内容
     * @return this
     */
    public HttpRequest form(Map<String, String> formMap) {
        if (CollectionUtil.isEmpty(formMap)) {
            return this;
        }

        formMap.forEach(this::form);
        return this;
    }

    /**
     * 设置内容主体<br>
     * 请求体body参数支持两种类型：
     *
     * <pre>
     * 1. 标准参数，例如 a=1&amp;b=2 这种格式
     * 2. Rest模式，此时body需要传入一个JSON或者XML字符串，会自动绑定其对应的Content-Type
     * </pre>
     *
     * @param body 请求体
     * @return this
     */
    public HttpRequest body(String body) {
        return this.body(body, null);
    }

    /**
     * 设置内容主体<br>
     * 请求体body参数支持两种类型：
     *
     * <pre>
     * 1. 标准参数，例如 a=1&amp;b=2 这种格式
     * 2. Rest模式，此时body需要传入一个JSON或者XML字符串，会自动绑定其对应的Content-Type
     * </pre>
     *
     * @param body        请求体
     * @param contentType 请求体类型，{@code null}表示自动判断类型
     * @return this
     */
    public HttpRequest body(String body, String contentType) {
        if (null != contentType) {
            // Content-Type自定义设置
            this.setContentType(contentType);
        } else {
            Header header = this.requestBuilder.getLastHeader(HttpHeaders.CONTENT_TYPE);
            // 未自定义Content-Type时，自动判断
            contentType = header == null ? analyzeContentType(body) : header.getValue();
        }

        // Content-Type为空时，使用默认的TEXT_PLAIN
        if (contentType == null) {
            this.requestBuilder.setEntity(new StringEntity(body, this.requestBuilder.getCharset()));
        } else {
            this.requestBuilder.setEntity(new StringEntity(body, ContentType.create(contentType, this.requestBuilder.getCharset())));
        }
        return this;
    }

    // ---------------------------------------------------------------- Body end

    /**
     * 设置内容主体<br>
     *
     * @param bodyBytes 请求体
     * @return this
     */
    public HttpRequest body(byte[] bodyBytes) {
        this.requestBuilder.setEntity(new ByteArrayEntity(Optional.ofNullable(bodyBytes).orElse(new byte[NumberConstant.INTEGER_ZERO])));
        return this;
    }

    /**
     * 设置内容主体<br>
     *
     * @param entity 请求体
     * @return this
     */
    public HttpRequest setEntity(final HttpEntity entity) {
        this.requestBuilder.setEntity(entity);
        return this;
    }

    /**
     * 执行请求
     *
     * @return 响应
     */
    public HttpResponse execute() throws IOException {
        // 设置连接超时配置
        this.requestBuilder.setConfig(RequestConfig.custom()
                .setSocketTimeout(httpConnectConfig.getSocketTimeout())
                .setConnectTimeout(httpConnectConfig.getConnectTimeout())
                .setConnectionRequestTimeout(httpConnectConfig.getConnectionRequestTimeout())
                .build());
        return new HttpResponse(HttpConnection.getInstance().getHttpClient().execute(this.requestBuilder.build()));
    }
}
