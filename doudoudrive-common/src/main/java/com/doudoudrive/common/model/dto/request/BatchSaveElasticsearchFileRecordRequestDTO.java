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
 * <p>批量保存es文件临时操作记录信息时的请求数据模型</p>
 * <p>2023-07-28 11:06</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchSaveElasticsearchFileRecordRequestDTO {

    /**
     * 批量保存es文件临时操作记录信息时的请求数据模型
     */
    @Valid
    @NotEmpty(message = "参数集合为空")
    @Size(max = 1000, message = "请不要一次性操作太多数据~")
    private List<SaveElasticsearchFileRecordRequestDTO> fileRecordInfo;

}
