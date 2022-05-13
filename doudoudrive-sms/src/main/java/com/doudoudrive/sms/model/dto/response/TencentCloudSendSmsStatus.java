package com.doudoudrive.sms.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>腾讯云短信发送状态响应对象</p>
 * <p>2022-05-07 10:05</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TencentCloudSendSmsStatus implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 发送流水号。
     */
    private String serialNo;

    /**
     * 手机号码，E.164标准，+[国家或地区码][手机号] ，示例如：+8613711112222， 其中前面有一个+号 ，86为国家码，13711112222为手机号。
     */
    private String phoneNumber;

    /**
     * 计费条数，计费规则请查询 <a href="https://cloud.tencent.com/document/product/382/36135">计费策略</a>
     */
    private Long fee;

    /**
     * 用户 session 内容
     */
    private String sessionContext;

    /**
     * 短信请求错误码
     * 具体含义请参考 <a href="https://cloud.tencent.com/document/api/382/55981#6.-.E9.94.99.E8.AF.AF.E7.A0.81">错误码</a>
     * 发送成功返回 "Ok"
     */
    private String code;

    /**
     * 短信请求错误码描述。
     */
    private String message;

    /**
     * 国家码或地区码，例如 CN、US 等，对于未识别出国家码或者地区码，默认返回 DEF
     * 具体支持列表请参考 <a href="https://cloud.tencent.com/document/product/382/18051">国际/港澳台短信价格总览</a>
     */
    private String isoCode;


}
