package com.doudoudrive.common.model.dto.model.aliyun;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>阿里云CDN日志服务消费数据模型</p>
 * <p>参考：<a href="https://help.aliyun.com/zh/cdn/user-guide/fields-in-real-time-logs?spm=5176.11785003.help.dexternal.5556142fYiw7HB">实时日志推送字段说明</a></p>
 * <p>2024-03-31 18:37</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AliCloudCdnLogModel {
    /**
     * 请求时间，unix时间戳
     */
    @JSONField(name = "unixtime")
    private Long unixTime;

    /**
     * 请求的域名
     */
    @JSONField(name = "domain")
    private String domain;

    /**
     * 请求方法
     */
    @JSONField(name = "method")
    private String method;

    /**
     * 请求协议
     */
    @JSONField(name = "scheme")
    private String scheme;

    /**
     * 请求资源
     */
    @JSONField(name = "uri")
    private String uri;

    /**
     * 请求参数
     */
    @JSONField(name = "uri_param")
    private String uriParam;

    /**
     * 用户真实IP，从用户请求携带的请求头X-Forwarded-For中提取左边第一个IP地址
     */
    @JSONField(name = "client_ip")
    private String ip;

    /**
     * 代理IP。从用户请求携带的请求头X-Forwarded-For中提取左边第二个IP地址
     */
    @JSONField(name = "proxy_ip")
    private String proxyIp;

    /**
     * 和CDN节点建连IP
     */
    @JSONField(name = "remote_ip")
    private String remoteIp;

    /**
     * 和CDN节点建连客户端端口
     */
    @JSONField(name = "remote_port")
    private String remotePort;

    /**
     * HTTP refer中的协议
     */
    @JSONField(name = "refer_protocol")
    private String referProtocol;

    /**
     * HTTP refer中domain信息
     */
    @JSONField(name = "refer_domain")
    private String referDomain;

    /**
     * HTTP refer中uri信息
     */
    @JSONField(name = "refer_uri")
    private String referUri;

    /**
     * HTTP refer中的参数信息
     */
    @JSONField(name = "refer_param")
    private String referParam;

    /**
     * 请求大小
     */
    @JSONField(name = "request_size")
    private String requestSize;

    /**
     * 请求响应时间，单位：毫秒
     */
    @JSONField(name = "request_time")
    private Long requestTime;

    /**
     * 请求返回大小，单位：字节
     */
    @JSONField(name = "response_size")
    private String responseSize;

    /**
     * 请求响应码
     */
    @JSONField(name = "return_code")
    private String responseCode;

    /**
     * 应答头里表示的range信息（由源站创建），如bytes：0~99/200。
     */
    @JSONField(name = "sent_http_content_range")
    private String sentHttpContentRange;

    /**
     * 服务的CDN节点IP
     */
    @JSONField(name = "server_addr")
    private String serverAddr;

    /**
     * 服务的CDN节点服务端口
     */
    @JSONField(name = "server_port")
    private String serverPort;

    /**
     * 实际发送body大小，单位：字节
     */
    @JSONField(name = "body_bytes_sent")
    private String bodyBytesSent;

    /**
     * 请求的资源类型
     */
    @JSONField(name = "content_type")
    private String contentType;

    /**
     * 命中信息（直播，动态加速除外），取值为HIT（命中）、MISS（未命中）
     */
    @JSONField(name = "hit_info")
    private String hitInfo;

    /**
     * 用户请求中Header头中range字段取值，如bytes：0~100
     */
    @JSONField(name = "http_range")
    private String httpRange;

    /**
     * 用户代理信息
     */
    @JSONField(name = "user_agent")
    private String userAgent;

    /**
     * 请求唯一标识（全网唯一请求ID，即traceid）
     */
    @JSONField(name = "uuid")
    private String businessId;

    /**
     * via头信息
     */
    @JSONField(name = "via_info")
    private String viaInfo;

    /**
     * 请求头中XForwardFor字段
     */
    @JSONField(name = "xforwordfor")
    private String xForwardFor;

}
