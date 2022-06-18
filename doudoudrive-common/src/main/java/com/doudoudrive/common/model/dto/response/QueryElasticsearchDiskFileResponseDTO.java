package com.doudoudrive.common.model.dto.response;

import com.doudoudrive.common.model.dto.model.DiskFileModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>搜索es用户文件信息时的响应数据模型</p>
 * <p>2022-06-18 16:05</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryElasticsearchDiskFileResponseDTO {

    /**
     * 搜索结果
     */
    private DiskFileModel content;

    /**
     * 搜索时的排序值，用作下一页的游标
     */
    private List<Object> sortValues;

}
