package com.doudoudrive.common.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * <p>搜索es文件临时操作记录信息时的请求数据模型</p>
 * <p>2023-07-28 14:58</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryElasticsearchFileRecordRequestDTO {


    /**
     * 指定的用户标识
     */
    @Size(max = 35, message = "用户标识长度错误")
    private String userId;

    /**
     * 动作
     */
    @NotBlank(message = "动作标识不能为空")
    private String action;

    /**
     * 动作类型
     */
    @NotBlank(message = "动作类型不能为空")
    private String actionType;

    /**
     * 文件唯一标识
     */
    private String etag;

}
