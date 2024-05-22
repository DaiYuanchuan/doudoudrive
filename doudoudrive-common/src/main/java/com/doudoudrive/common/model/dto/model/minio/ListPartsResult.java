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
 * <p>API_ListParts: 列出指定上传 ID 的所有分片响应数据</p>
 * <p>2024-04-27 21:47</p>
 *
 * @author Dan
 * @see <a
 * href="https://docs.aws.amazon.com/zh_cn/AmazonS3/latest/API/API_ListParts.html">ListParts</a>
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ListPartsResult")
public class ListPartsResult {

    /**
     * 存储桶名称
     */
    @XmlElement(name = "Bucket")
    private String bucket;

    /**
     * 在原始请求中指定的对象键
     */
    @XmlElement(name = "Key")
    private String key;

    /**
     * 关联的multipart上传的发起者
     */
    @XmlElement(name = "Initiator")
    private Owner initiator;

    /**
     * 分片上传事件的所有者
     */
    @XmlElement(name = "Owner")
    private Owner owner;

    /**
     * 用于关联多部件上传中的部件的存储类
     */
    @XmlElement(name = "StorageClass")
    private String storageClass;

    /**
     * 在原始请求中指定的可选部件号标记，用于指定在结果中开始列出部件的位置。
     */
    @XmlElement(name = "PartNumberMarker")
    private Integer partNumberMarker;

    /**
     * 如果此列表被截断，则在下一个请求中应该使用此部件号标记来获得下一页的结果。
     */
    @XmlElement(name = "NextPartNumberMarker")
    private Integer nextPartNumberMarker;

    /**
     * 在原始请求中指定的可选最大查询数量
     */
    @XmlElement(name = "MaxParts")
    private Integer maxParts;

    /**
     * 指示列表是否被截断，以及是否需要发出其他请求以获得更多结果。
     */
    @XmlElement(name = "IsTruncated")
    private boolean isTruncated;

    /**
     * 分片列表信息
     */
    @XmlElement(name = "Part")
    private List<Part> partList;
}
