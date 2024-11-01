package com.doudoudrive.common.model.dto.model.minio;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.*;

/**
 * <p>用来表示Minio的对象信息</p>
 * <p>适用于 {@link ListObjectsV1}、{@link ListObjectsV2}.</p>
 * <p>2024-04-26 00:47</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MinioObject {

    /**
     * 对象的哈希值，ETag 仅反映对内容的更改
     * ETag 可能是也可能不是对象的 MD5 摘要，这取决于对象的创建方式以及如何将其加密
     */
    @Setter(onMethod = @__({@XmlElement(name = "ETag")}))
    private String etag;

    /**
     * 分配给对象的名称，使用对象键检索的对象
     */
    @Setter(onMethod = @__({@XmlElement(name = "Key")}))
    private String key;

    /**
     * 上次修改时间，就是对象的创建日期
     */
    @Setter(onMethod = @__({@XmlElement(name = "LastModified")}))
    private String lastModified;

    /**
     * 对象的所有者信息
     */
    @Setter(onMethod = @__({@XmlElement(name = "Owner")}))
    private Owner owner;

    /**
     * 对象的大小（以字节为单位）
     */
    @Setter(onMethod = @__({@XmlElement(name = "Size")}))
    private long size;

    /**
     * 用于存储对象的存储类型
     * STANDARD | REDUCED_REDUNDANCY 等
     */
    @Setter(onMethod = @__({@XmlElement(name = "StorageClass")}))
    private String storageClass;
}
