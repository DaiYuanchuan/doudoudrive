package com.doudoudrive.common.model.dto.model.minio;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.*;

/**
 * <p>删除对象</p>
 * <p>2024-04-26 18:17</p>
 *
 * @author Dan
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeleteObject {

    /**
     * 需要删除的对象key
     */
    @Setter(onMethod = @__({@XmlElement(name = "Key")}))
    private String key;

    /**
     * 如果需要删除的对象的指定版本信息，则需要指定版本号
     */
    @Setter(onMethod = @__({@XmlElement(name = "VersionId")}))
    private String versionId;
}
