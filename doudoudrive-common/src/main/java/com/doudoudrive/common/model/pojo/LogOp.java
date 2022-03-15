package com.doudoudrive.common.model.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>API操作日志实体类</p>
 * <p>2022-03-06 14:36</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LogOp implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 自增长标识
     */
    private Long autoId;

    /**
     * API操作日志系统内唯一标识
     */
    private String businessId;

    /**
     * 当前访问的ip地址
     */
    private String ip;

    /**
     * ip地址的实际地理位置
     */
    private String location;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 执行操作的类名称
     */
    private String className;

    /**
     * 执行操作的方法名称
     */
    private String methodName;

    /**
     * 请求时的url参数
     */
    private String parameter;

    /**
     * 爬虫的类型(如果有)
     */
    private String spider;

    /**
     * 操作系统类型
     */
    private String os;

    /**
     * 操作平台类型
     */
    private String platform;

    /**
     * 浏览器内核类型
     */
    private String browser;

    /**
     * 浏览器内核版本
     */
    private String browserVersion;

    /**
     * 浏览器解析引擎的类型
     */
    private String browserEngine;

    /**
     * 浏览器解析引擎的版本
     */
    private String browserEngineVersion;

    /**
     * 发出此请求的HTTP方法的名称(如 GET|POST|PUT)
     */
    private String method;

    /**
     * 浏览器的UA标识
     */
    private String userAgent;

    /**
     * 是否为移动平台(0:false,1:true)
     */
    private Boolean mobile;

    /**
     * 访问的URL除去host部分的路径
     */
    private String requestUri;

    /**
     * 请求头中的referer信息
     */
    private String referer;

    /**
     * 模块名称
     */
    private String title;

    /**
     * 访问出现错误时获取到的异常信息
     */
    private String errorMsg;

    /**
     * 访问出现错误时获取到的异常原因
     */
    private String errorCause;

    /**
     * 访问的状态(0:false,1:true)
     */
    private Boolean success;

    /**
     * 访问时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 当前操作的用户名
     */
    private String username;

}
