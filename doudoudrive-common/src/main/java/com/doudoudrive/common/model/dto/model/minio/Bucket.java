package com.doudoudrive.common.model.dto.model.minio;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>存储桶信息</p>
 * <p>2024-04-23 19:42</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "Bucket")
@XmlAccessorType(XmlAccessType.FIELD)
public class Bucket implements Serializable {

    @Serial
    private static final long serialVersionUID = 1098133275721008301L;

    /**
     * 桶名称
     */
    @XmlElement(name = "Name")
    private String name;

    /**
     * 创建时间
     */
    @XmlJavaTypeAdapter(MinioResponseDateAdapter.class)
    @XmlElement(name = "CreationDate")
    private LocalDateTime creationDate;
}
