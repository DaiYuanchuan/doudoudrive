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
 * <p>批量更新es用户文件信息时的请求数据模型</p>
 * <p>2023-01-05 22:19</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBatchElasticsearchDiskFileRequestDTO {

    /**
     * 批量更新es用户文件信息时的请求数据模型
     */
    @Valid
    @NotEmpty(message = "参数集合为空")
    @Size(max = 1000, message = "请不要一次性操作太多数据~")
    private List<UpdateElasticsearchDiskFileRequestDTO> fileInfo;

}
