package com.doudoudrive.common.model.dto.model.minio;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.*;

import java.util.List;

/**
 * <p>存储桶中的对象列表v2</p>
 * <p>2024-04-24 19:16</p>
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
public class ListObjectsV2 extends ListObjectsResult {

    /**
     * 随请求返回的列表数量，将始终小于或等于字段
     */
    @XmlElement(name = "KeyCount")
    private Integer keyCount;

    /**
     * 从指定键之后开始返回键，
     * 在此之后开始查询 指定的键，StartAfter 可以是存储桶中的任何键
     * 仅适用于ListObjectsV2
     */
    @XmlElement(name = "StartAfter")
    private String startAfter;

    /**
     * 分页标识，可以对列表的结果进行分页
     * 仅适用于ListObjectsV2接口
     */
    @XmlElement(name = "ContinuationToken")
    private String continuationToken;

    /**
     * 下一个分页标识
     */
    @XmlElement(name = "NextContinuationToken")
    private String nextContinuationToken;

    /**
     * 对象内容
     */
    @XmlElement(name = "Contents")
    private List<MinioObject> contents;
}
