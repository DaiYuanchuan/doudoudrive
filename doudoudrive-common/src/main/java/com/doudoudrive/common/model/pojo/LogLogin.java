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
 * <p>登录日志实体类</p>
 * <p>2022-03-06 14:31</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LogLogin implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 自增长标识
     */
    private Long autoId;

    /**
     * 登录日志系统内唯一标识
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
     * 浏览器的UA标识
     */
    private String userAgent;

    /**
     * 是否为移动平台(0:false,1:true)
     */
    private Boolean mobile;

    /**
     * 操作系统类型
     */
    private String os;

    /**
     * 操作平台类型
     */
    private String platform;

    /**
     * 登陆的用户名
     */
    private String username;

    /**
     * 是否登录成功(0:false,1:true)
     */
    private Boolean success;

    /**
     * 提示消息
     */
    private String msg;

    /**
     * 当前用户登录请求的sessionId
     */
    private String sessionId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

}
