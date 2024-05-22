package com.doudoudrive.common.model.dto.model.minio;

import com.doudoudrive.common.constant.ConstantConfig;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

/**
 * <p>API_CopyObject: 复制一个对象响应</p>
 * <p>2024-04-27 23:37</p>
 *
 * @author Dan
 * @see <a
 * href="https://docs.aws.amazon.com/zh_cn/AmazonS3/latest/API/API_CopyObject.html">CopyObject</a>
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "CopyObjectResult")
public class CopyObjectResult {

    /**
     * etag
     */
    @XmlElement(name = "ETag")
    private String etag;

    /**
     * 对象最后修改时间
     */
    @XmlElement(name = "LastModified")
    @XmlJavaTypeAdapter(MinioResponseDateAdapter.class)
    private LocalDateTime lastModified;

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
