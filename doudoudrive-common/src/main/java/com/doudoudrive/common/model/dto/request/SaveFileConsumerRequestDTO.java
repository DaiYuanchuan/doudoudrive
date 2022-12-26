package com.doudoudrive.common.model.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * <p>保存文件时的消费者请求数据模型</p>
 * <p>2022-12-26 14:43</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SaveFileConsumerRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 批量保存es用户文件信息时的请求数据模型
     */
    private List<SaveElasticsearchDiskFileRequestDTO> fileInfo;
}
