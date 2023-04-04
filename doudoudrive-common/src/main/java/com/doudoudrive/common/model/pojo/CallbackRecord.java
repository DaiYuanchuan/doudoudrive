package com.doudoudrive.common.model.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>外部系统回调记录实体类</p>
 * <p>2023-03-30 14:32:28</p>
 *
 * @author Dan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CallbackRecord implements Serializable {

    @Serial
    private static final long serialVersionUID = 7913237989174530629L;

    /**
     * 自增长标识
     */
    private Long autoId;

    /**
     * 业务标识
     */
    private String businessId;

    /**
     * 请求地址
     */
    private String httpUrl;

    /**
     * 请求的参数
     */
    private String requestBody;

    /**
     * 请求的http状态码
     */
    private String httpStatus;

    /**
     * 请求的响应体
     */
    private String responseBody;

    /**
     * 请求耗时，单位毫秒
     */
    private Long costTime;

    /**
     * 重试次数，最多重试3次
     */
    private Integer retry;

    /**
     * 请求回调时间
     */
    private LocalDateTime sendTime;

    /**
     * 请求回调状态(1:等待；2:执行中；3:回调成功；4:回调失败)
     */
    private String sendStatus;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
