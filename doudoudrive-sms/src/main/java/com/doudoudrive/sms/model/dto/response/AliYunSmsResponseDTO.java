package com.doudoudrive.sms.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>阿里大鱼短信发送请求响应数据模型</p>
 * <p>2022-04-29 19:16</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AliYunSmsResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 请求ID，类似F655A8D5-B967-440B-8683-DAD6FF8DE990
     */
    private String requestId;

    /**
     * 状态码的描述，OK
     */
    private String message;

    /**
     * 发送回执ID
     * 可根据发送回执ID在接口QuerySendDetails中查询具体的发送状态
     */
    private String bizId;

    /**
     * 请求状态码，返回 OK 代表请求成功
     */
    private String code;

}
