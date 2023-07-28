package com.doudoudrive.common.model.dto.response;

import com.doudoudrive.common.model.dto.model.FileRecordModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>搜索es文件临时操作记录信息时的响应数据模型</p>
 * <p>2023-07-28 15:04</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryElasticsearchFileRecordResponseDTO {

    /**
     * 搜索结果
     */
    private FileRecordModel content;

    /**
     * 搜索时的排序值，用作下一页的游标
     */
    private List<Object> sortValues;
}
