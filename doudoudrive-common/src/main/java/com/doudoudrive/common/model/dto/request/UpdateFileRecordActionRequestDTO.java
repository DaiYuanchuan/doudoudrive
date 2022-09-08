package com.doudoudrive.common.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * <p>更新指定动作的文件操作记录数据状态的请求数据模型</p>
 * <p>2022-09-08 16:12</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFileRecordActionRequestDTO {

    /**
     * 文件操作记录系统内唯一标识
     */
    @NotBlank(message = "业务标识不能为空")
    @Size(min = 1, max = 35, message = "业务标识长度错误")
    private String businessId;

    /**
     * 动作枚举
     */
    @NotBlank(message = "动作枚举不能为空")
    private String action;

    /**
     * 动作对应的动作类型
     */
    private String actionType;

}
