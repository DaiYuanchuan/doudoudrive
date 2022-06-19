package com.doudoudrive.file.model.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>创建文件时消费者回调请求数据模型</p>
 * <p>2022-05-22 21:27</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateFileCallbackRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文件名称
     */
    private String name;

    /**
     * 文件标识
     */
    private String fileId;

    /**
     * 文件父级标识
     */
    private String fileParentId;

    /**
     * 文件大小(字节)
     */
    private String fileSize;

    /**
     * 文件的mime类型
     */
    private String fileMimeType;

    /**
     * 文件的ETag(资源的唯一标识)
     */
    private String fileEtag;

    /**
     * 时间戳，记录文件上传时间
     */
    private Long timestamp;

}
