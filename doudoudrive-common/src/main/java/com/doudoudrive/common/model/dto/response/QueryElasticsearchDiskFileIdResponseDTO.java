package com.doudoudrive.common.model.dto.response;

import com.doudoudrive.common.model.dto.model.DiskFileModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>搜索es用户文件Id数据时的响应数据模型</p>
 * <p>2022-08-16 18:09</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryElasticsearchDiskFileIdResponseDTO {

    /**
     * 搜索结果
     */
    private List<DiskFileModel> content;

}
