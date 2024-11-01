package com.doudoudrive.common.model.dto.request;

import com.doudoudrive.common.model.dto.model.auth.CreateFileAuthModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>创建文件时的消费者请求数据模型</p>
 * <p>2022-05-25 20:13</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateFileConsumerRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 当前文件标识
     */
    private String fileId;

    /**
     * 文件预览地址
     */
    private String preview;

    /**
     * 文件下载地址
     */
    private String download;

    /**
     * 创建文件时的鉴权参数模型
     */
    private CreateFileAuthModel fileInfo;

}
