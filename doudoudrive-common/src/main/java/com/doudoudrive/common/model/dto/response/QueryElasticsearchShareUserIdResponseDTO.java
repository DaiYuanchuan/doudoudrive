package com.doudoudrive.common.model.dto.response;

import com.doudoudrive.common.model.dto.model.FileShareModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>根据用户标识搜索es文件分享记录信息时的响应数据模型</p>
 * <p>2022-09-27 22:02</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryElasticsearchShareUserIdResponseDTO {

    /**
     * 搜索查询结果
     */
    private FileShareModel content;

    /**
     * 搜索时的排序值，用作下一页的游标
     */
    private List<Object> sortValues;

}
