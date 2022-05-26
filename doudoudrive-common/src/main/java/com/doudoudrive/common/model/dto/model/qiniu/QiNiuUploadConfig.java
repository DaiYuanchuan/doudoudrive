package com.doudoudrive.common.model.dto.model.qiniu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>七牛云上传数据配置模型，转供七牛云对象存储使用</p>
 * <p>2022-05-25 11:51</p>
 *
 * @author Dan
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QiNiuUploadConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文件上传成功后的回调地址，这里需要是公网地址
     */
    private String callback;

    /**
     * 对象存储中的路径(不会自动创建此路径，需要手动创建，{} 为占位符)
     */
    private String path;

    /**
     * 七牛云accessKey信息，<a href="https://portal.qiniu.com/user/key">查看</a>
     */
    private String accessKey;

    /**
     * 七牛云secretKey信息，<a href="https://portal.qiniu.com/user/key">查看</a>
     */
    private String secretKey;

    /**
     * 七牛云存储桶名称
     */
    private String bucket;

    /**
     * 七牛云CDN加速域名地址相关配置
     */
    private QiNiuDomainConfig domain;

    /**
     * 存储区域相关配置
     */
    private QiNiuRegionConfig region;

}
