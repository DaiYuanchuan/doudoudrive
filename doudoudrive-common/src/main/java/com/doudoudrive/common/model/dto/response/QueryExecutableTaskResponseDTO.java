package com.doudoudrive.common.model.dto.response;

import com.doudoudrive.common.model.dto.model.FileRecordModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>获取可执行任务时的响应数据模型</p>
 * <p>2022-09-08 14:56</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryExecutableTaskResponseDTO {

    /**
     * 文件操作记录数据模型
     */
    private FileRecordModel content;

}
