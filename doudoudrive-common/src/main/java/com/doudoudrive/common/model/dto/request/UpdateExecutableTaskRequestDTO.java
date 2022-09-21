package com.doudoudrive.common.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Optional;

/**
 * <p>更新正在执行中的任务时的请求数据模型</p>
 * <p>2022-09-08 19:59</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateExecutableTaskRequestDTO {

    /**
     * 文件操作记录系统内唯一标识
     */
    @NotBlank(message = "任务标识不能为空")
    @Size(max = 35, message = "任务标识长度错误")
    private String businessId;

    /**
     * 动作枚举
     */
    @NotBlank(message = "动作枚举不能为空")
    private String action;

    /**
     * 任务执行结果，默认为true
     * true: 成功；false: 失败
     */
    private Boolean success;

    /**
     * 获取任务执行结果，默认为true
     *
     * @return 任务执行结果
     */
    public Boolean getSuccess() {
        return Optional.ofNullable(success).orElse(Boolean.TRUE);
    }
}
