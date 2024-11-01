package com.doudoudrive.common.model.dto.model.minio;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>桶的所有者信息</p>
 * <p>2024-04-23 19:36</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "Owner")
@XmlAccessorType(XmlAccessType.FIELD)
public class Owner implements Serializable {

    @Serial
    private static final long serialVersionUID = -7291076917295151021L;

    /**
     * id
     */
    @XmlElement(name = "ID")
    private String id;

    /**
     * 名称
     */
    @XmlElement(name = "DisplayName")
    private String displayName;

}
