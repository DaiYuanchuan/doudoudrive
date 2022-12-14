package com.doudoudrive.common.model.dto.response;

import com.doudoudrive.common.model.dto.model.FileShareModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>搜索es用户文件分享标识数据时的响应数据模型</p>
 * <p>2022-09-28 00:04</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryElasticsearchFileShareIdResponseDTO {

    /**
     * 搜索结果
     */
    private List<FileShareModel> content;

}
