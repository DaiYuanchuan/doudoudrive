package com.doudoudrive.common.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>删除es信息时的通用响应数据模型</p>
 * <p>2023-07-28 11:54</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteElasticsearchResponseDTO {

    /**
     * 已成功删除的文档数量
     */
    private Long deleted;

    /**
     * 整个操作从开始到结束的耗时，单位为毫秒
     */
    private Long took;

}
