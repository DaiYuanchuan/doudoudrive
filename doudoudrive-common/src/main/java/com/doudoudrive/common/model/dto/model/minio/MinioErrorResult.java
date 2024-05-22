package com.doudoudrive.common.model.dto.model.minio;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>Minio请求通用异常响应结果</p>
 * <p>2024-04-24 14:42</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "Error")
@XmlAccessorType(XmlAccessType.FIELD)
public class MinioErrorResult implements Serializable {

    @Serial
    private static final long serialVersionUID = -847377182417330477L;

    /**
     * 方法名称
     */
    @XmlElement(name = "Code")
    private String code;

    /**
     * 消息具体内容
     */
    @XmlElement(name = "Message")
    private String message;

    /**
     * 对应的存储桶名称
     */
    @XmlElement(name = "BucketName")
    private String bucket;

    /**
     * 对应的资源名称
     */
    @XmlElement(name = "Resource")
    private String resource;

    /**
     * 请求id
     */
    @XmlElement(name = "RequestId")
    private String requestId;

    /**
     * 主机id
     */
    @XmlElement(name = "HostId")
    private String hostId;
}
