package com.doudoudrive.sms.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>腾讯云短信发送请求响应数据模型</p>
 * <p>2022-05-06 22:57</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TencentCloudSmsResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 腾讯云短信发送请求响应对象
     */
    private TencentCloudSendSmsResponse response;


}
