package com.doudoudrive.sms.model.dto.response;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * <p>腾讯云短信发送请求响应对象</p>
 * <p>2022-05-06 22:58</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TencentCloudSendSmsResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 腾讯云短信发送状态。
     */
    @JSONField(alternateNames = {"SendStatusSet", "Error"})
    private List<TencentCloudSendSmsStatus> sendStatus;

    /**
     * 唯一请求 ID，每次请求都会返回。定位问题时需要提供该次请求的 RequestId。
     */
    private String requestId;

}
