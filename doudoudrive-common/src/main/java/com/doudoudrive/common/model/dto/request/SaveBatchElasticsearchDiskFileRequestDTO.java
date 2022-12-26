package com.doudoudrive.common.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * <p>批量保存es用户文件信息时的请求数据模型</p>
 * <p>2022-12-26 14:21</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveBatchElasticsearchDiskFileRequestDTO {

    /**
     * 批量保存es用户文件信息时的请求数据模型
     */
    @Valid
    @NotEmpty(message = "参数集合为空")
    @Size(max = 1000, message = "请不要一次性操作太多数据~")
    private List<SaveElasticsearchDiskFileRequestDTO> fileInfo;

}
