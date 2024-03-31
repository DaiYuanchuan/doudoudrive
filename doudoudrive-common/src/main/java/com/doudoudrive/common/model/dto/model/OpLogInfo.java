package com.doudoudrive.common.model.dto.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.servlet.http.HttpServletRequest;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>操作日志注解对应的数据模型</p>
 * <p>2022-03-14 22:36</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpLogInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 当前访问的ip地址
     */
    private String ip;

    /**
     * ip地址的实际地理位置
     */
    private String location;

    /**
     * 访问的URL获取除去host部分的路径
     */
    private String requestUri;

    /**
     * 模块名称
     */
    private String title;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 发出此请求的HTTP方法的名称(如 GET|POST|PUT)
     */
    private String method;

    /**
     * HTTP请求对象
     */
    private HttpServletRequest request;

    /**
     * 执行操作的类名称
     */
    private String className;

    /**
     * 执行操作的方法名称
     */
    private String methodName;

    /**
     * url参数
     */
    private String parameter;

    /**
     * 请求大小，单位字节
     */
    private Integer requestSize;

    /**
     * 当前请求时间
     */
    private LocalDateTime requestTime;

    /**
     * 请求的资源类型
     */
    private String contentType;

    /**
     * 浏览器内核类型
     */
    private String browser;

    /**
     * 浏览器内核版本
     */
    private String browserVersion;

    /**
     * 浏览器的解析引擎类型
     */
    private String browserEngine;

    /**
     * 浏览器的解析引擎版本
     */
    private String browserEngineVersion;

    /**
     * 浏览器UA标识
     */
    private String userAgent;

    /**
     * 是否为移动平台
     */
    private Boolean isMobile;

    /**
     * 操作系统类型
     */
    private String os;

    /**
     * 操作平台类型
     */
    private String platform;

    /**
     * 爬虫的类型(如果有)
     */
    private String spider;

    /**
     * 访问出现错误时获取到的异常原因
     */
    private String errorCause;

    /**
     * 访问出现错误时获取到的异常信息
     */
    private String errorMsg;

    /**
     * 访问的状态(0:正常,1:不正常)
     */
    private Boolean success;

    /**
     * 访问的时间
     */
    private Date createTime;

    /**
     * 当前响应时间
     */
    private LocalDateTime responseTime;

    /**
     * 响应大小，单位字节
     */
    private Integer responseSize;

    /**
     * 响应状态码
     */
    private Integer responseCode;

    /**
     * 请求耗时，单位毫秒
     */
    private Long costTime;

}