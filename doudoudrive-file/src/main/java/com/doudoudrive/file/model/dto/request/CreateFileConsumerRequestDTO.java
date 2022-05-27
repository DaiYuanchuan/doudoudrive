package com.doudoudrive.file.model.dto.request;

import com.doudoudrive.common.model.dto.model.CreateFileAuthModel;
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
     * 当前文件标识，确保消息幂等
     */
    private String fileId;

    /**
     * 用户当前token，会尝试使用token去更新
     */
    private String token;

    /**
     * 创建文件时的鉴权参数模型
     */
    private CreateFileAuthModel fileInfo;

}
