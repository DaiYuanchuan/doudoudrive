package com.doudoudrive.common.model.dto.model.minio;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>API_CreateMultipartUpload：创建一个分片上传请求</p>
 * <p>获取上传id，可以用于分片上传</p>
 * <p>2024-04-27 18:37</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "InitiateMultipartUploadResult")
public class CreateMultipartUploadResult {

    /**
     * 存储桶名称
     */
    @XmlElement(name = "Bucket")
    private String bucket;

    /**
     * 上传的对象名称
     */
    @XmlElement(name = "Key")
    private String key;

    /**
     * 上传id
     */
    @XmlElement(name = "UploadId")
    private String uploadId;

}
