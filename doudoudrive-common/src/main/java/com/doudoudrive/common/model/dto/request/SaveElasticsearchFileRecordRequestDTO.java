package com.doudoudrive.common.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * <p>保存es文件临时操作记录信息时的请求数据模型</p>
 * <p>2023-07-28 11:04</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveElasticsearchFileRecordRequestDTO {

    /**
     * 用户系统内唯一标识
     */
    @NotBlank(message = "用户标识不能为空")
    @Size(min = 1, max = 35, message = "用户标识长度错误")
    private String userId;

    /**
     * 文件标识
     */
    @NotBlank(message = "文件标识不能为空")
    @Size(min = 1, max = 35, message = "文件标识长度错误")
    private String fileId;

    /**
     * 文件的ETag(资源的唯一标识)
     */
    @NotBlank(message = "etag标识不能为空")
    private String fileEtag;

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

}
