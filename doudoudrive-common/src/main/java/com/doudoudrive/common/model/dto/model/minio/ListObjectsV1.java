package com.doudoudrive.common.model.dto.model.minio;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.*;

import java.util.List;

/**
 * <p>存储桶中的对象列表v1</p>
 * <p>2024-04-24 19:12</p>
 *
 * @author Dan
 **/
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ListBucketResult")
public class ListObjectsV1 extends ListObjectsResult {

    /**
     * 标记，用于标识查询的起始位置，和startAfter一样
     * 仅适用于ListObjectsV1接口
     */
    @XmlElement(name = "Marker")
    private String marker;

    /**
     * 下一个标记，用于查询下一页
     */
    @XmlElement(name = "NextMarker")
    private String nextMarker;

    /**
     * 对象内容
     */
    @XmlElement(name = "Contents")
    private List<MinioObject> contents;

}
