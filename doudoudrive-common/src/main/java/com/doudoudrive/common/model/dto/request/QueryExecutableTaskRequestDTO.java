package com.doudoudrive.common.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * <p>获取可执行任务时的请求数据模型</p>
 * <p>2022-09-08 14:41</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryExecutableTaskRequestDTO {

    /**
     * 动作枚举
     */
    @NotBlank(message = "动作枚举不能为空")
    private String action;

}
