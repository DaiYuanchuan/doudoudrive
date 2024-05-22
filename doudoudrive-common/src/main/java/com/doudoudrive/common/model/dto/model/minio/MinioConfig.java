package com.doudoudrive.common.model.dto.model.minio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>Minio上传数据配置模型，供Minio对象存储使用</p>
 * <p>2024-04-22 23:04</p>
 *
 * @author Dan
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MinioConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 7783954253030972027L;

    /**
     * Api 端点
     * <p>例如：<a href="https://s3-us-east-1.amazonaws.com">https://s3-us-east-1.amazonaws.com</a></p>
     */
    private String endpoint;

    /**
     * 存储桶名称
     */
    private String bucket;

    /**
     * 区域代码（例如，us-east-1）
     */
    private String region;

    /**
     * 访问密钥
     */
    private String accessKey;

    /**
     * 签名密钥
     */
    private String secretKey;

    /**
     * 服务名称（例如，s3）
     */
    private String server;

    /**
     * 对象存储中的路径(不会自动创建此路径，需要手动创建，{} 为占位符)
     */
    private String path;

}
