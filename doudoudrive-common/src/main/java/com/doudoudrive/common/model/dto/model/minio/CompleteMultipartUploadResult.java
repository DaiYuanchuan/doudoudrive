package com.doudoudrive.common.model.dto.model.minio;

import com.doudoudrive.common.constant.ConstantConfig;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * <p>API_CompleteMultipartUpload：完成分片上传响应结果</p>
 * <p>2024-04-27 21:06</p>
 *
 * @author Dan
 * @see <a
 * href="https://docs.aws.amazon.com/zh_cn/AmazonS3/latest/API/API_CompleteMultipartUpload.html">CompleteMultipartUpload</a>
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "CompleteMultipartUploadResult")
public class CompleteMultipartUploadResult {

    /**
     * 地址
     */
    @XmlElement(name = "Location")
    private String location;

    /**
     * 存储桶名称
     */
    @XmlElement(name = "Bucket")
    private String bucket;

    /**
     * 对象键
     */
    @XmlElement(name = "Key")
    private String key;

    /**
     * etag
     */
    @XmlElement(name = "ETag")
    private String etag;

    /**
     * 获取etag，去除双引号
     */
    public String getEtag() {
        if (StringUtils.isBlank(etag)) {
            return StringUtils.EMPTY;
        }
        return etag.replaceAll(ConstantConfig.SpecialSymbols.DOUBLE_QUOTATION_MARKS, StringUtils.EMPTY);
    }
}
