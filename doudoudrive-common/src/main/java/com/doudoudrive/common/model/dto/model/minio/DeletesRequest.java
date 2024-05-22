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
 * <p>API_DeleteObjects：删除对象请求</p>
 * <p>2024-04-26 23:01</p>
 *
 * @author Dan
 * @see <a
 * href="https://docs.aws.amazon.com/zh_cn/AmazonS3/latest/API/API_DeleteObjects.html">删除对象</a>
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "Delete")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeletesRequest {

    /**
     * 需要删除的对象列表
     */
    @XmlElement(name = "Object")
    private List<DeleteObject> objectList;

    /**
     * 是否启用安静模式
     * 该操作支持两种响应模式：详细模式和安静模式，默认为详细模式
     * 详细模式：在响应中，将为每个成功删除的对象返回一个DeleteObjectResult元素
     * 安静模式：在安静模式下，删除对象时只返回一个状态码，表示操作的成功或失败
     */
    @XmlElement(name = "Quiet")
    public Boolean isQuiet() {
        return Boolean.TRUE;
    }
}
