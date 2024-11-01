package com.doudoudrive.common.model.dto.model.minio;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>API_CompleteMultipartUpload：完成分片上传</p>
 * <p>2024-04-27 20:42</p>
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
@XmlRootElement(name = "CompleteMultipartUpload")
public class CompleteMultipartUpload {

    /**
     * 分片信息
     */
    @XmlElement(name = "Part")
    private List<Part> partList;

}
