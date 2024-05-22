package com.doudoudrive.common.model.dto.model.minio;

import com.doudoudrive.common.constant.ConstantConfig;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

/**
 * <p>表示分片上传时的分片信息，适用于：{@link CompleteMultipartUpload} and {@link ListPartsResult}</p>
 * <p>2024-04-27 20:37</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Part {

    /**
     * 分片号
     */
    @Setter(onMethod = @__({@XmlElement(name = "PartNumber")}))
    private Long partNumber;

    /**
     * 分片的ETag
     */
    private String etag;

    /**
     * 分片的最后修改时间
     */
    @Setter(onMethod = @__({@XmlElement(name = "LastModified"), @XmlJavaTypeAdapter(MinioResponseDateAdapter.class)}))
    private LocalDateTime lastModified;

    /**
     * 分片的大小
     */
    @Setter(onMethod = @__({@XmlElement(name = "Size")}))
    private Long size;

    @XmlElement(name = "ETag")
    public void setEtag(String etag) {
        if (StringUtils.isNotBlank(etag)) {
            etag = etag.replaceAll(ConstantConfig.SpecialSymbols.DOUBLE_QUOTATION_MARKS, StringUtils.EMPTY);
        }
        this.etag = etag;
    }
}
