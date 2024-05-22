package com.doudoudrive.common.model.dto.model.minio;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * <p>API_ListBuckets：列出当前所有的桶</p>
 * <p>2024-04-23 19:26</p>
 * <pre>
 *     (XmlAccessType.FIELD):类中的每个非静态、非瞬态字段将会自动绑定到 XML，除非由 XmlTransient 注释
 *     XmlRootElement:xml根标签名
 * </pre>
 *
 * @author Dan
 * @see <a
 * href="https://docs.aws.amazon.com/zh_cn/AmazonS3/latest/API/API_ListBuckets.html">ListBuckets</a>
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ListAllMyBucketsResult")
public class ListAllMyBucketsResult implements Serializable {

    @Serial
    private static final long serialVersionUID = -7539981958179301726L;

    /**
     * 所有者信息
     */
    @XmlElement(name = "Owner")
    private Owner owner;

    /**
     * 存储桶列表
     * XmlElementWrapper: 可用于指定List等集合类的外围标签名
     */
    @XmlElementWrapper(name = "Buckets")
    @XmlElement(name = "Bucket")
    private List<Bucket> buckets;

}
