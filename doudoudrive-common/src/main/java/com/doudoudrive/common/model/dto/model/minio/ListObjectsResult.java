package com.doudoudrive.common.model.dto.model.minio;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlTransient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>存储桶中的对象列表</p>
 * <p>适用于 {@link ListObjectsV1}、{@link ListObjectsV2}.</p>
 * <p>2024-04-24 17:45</p>
 *
 * @author Dan
 **/
@Data
@XmlTransient
@NoArgsConstructor
@AllArgsConstructor
public class ListObjectsResult {

    /**
     * 当前存储桶名称
     */
    @XmlElement(name = "Name")
    private String name;

    /**
     * 用于对 XML 响应中的对象键名称进行编码的编码类型。
     * 如果指定了请求参数，则在响应中返回编码的键名值
     */
    @XmlElement(name = "EncodingType")
    private String encodingType;

    /**
     * 前缀，将响应限制为以指定前缀开头的键
     */
    @XmlElement(name = "Prefix")
    private String prefix;

    /**
     * 分隔符是用于对键进行分组的字符
     * 对于目录存储桶，唯一受支持的分隔符: /
     */
    @XmlElement(name = "Delimiter")
    private String delimiter;

    /**
     * 是否截断
     * 如果已经返回了所有数据，则为 false
     * 如果还有更多数据，则为 true
     */
    @XmlElement(name = "IsTruncated")
    private boolean isTruncated;

    /**
     * 设置响应中返回的最大键数量
     * 默认情况下，该操作将返回多达 1000 个键名
     */
    @XmlElement(name = "MaxKeys")
    private Integer maxKeys;
}
