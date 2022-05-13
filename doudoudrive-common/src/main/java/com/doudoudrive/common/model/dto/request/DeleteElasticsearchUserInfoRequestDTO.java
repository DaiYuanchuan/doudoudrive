package com.doudoudrive.common.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * <p>删除es用户信息时的请求数据模型</p>
 * <p>2022-03-21 14:09</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteElasticsearchUserInfoRequestDTO {

    /**
     * 用户系统内唯一标识，也是es的id值，根据此值删除es数据
     */
    @NotBlank(message = "业务标识不能为空")
    @Size(min = 1, max = 35, message = "业务标识长度错误")
    private String businessId;

}
