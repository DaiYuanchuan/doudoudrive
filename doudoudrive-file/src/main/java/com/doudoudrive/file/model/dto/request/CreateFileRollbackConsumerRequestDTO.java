package com.doudoudrive.file.model.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>创建文件失败时异步回滚的消费者请求数据模型</p>
 * <p>2022-10-11 00:06</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateFileRollbackConsumerRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户系统内唯一标识
     */
    private String userId;

    /**
     * 文件标识
     */
    private String fileId;

    /**
     * 文件大小(字节)
     */
    private String size;

    /**
     * 重试次数，默认为0，大于0时表示重试，最大重试次数为3
     */
    private Integer retryCount;

}
