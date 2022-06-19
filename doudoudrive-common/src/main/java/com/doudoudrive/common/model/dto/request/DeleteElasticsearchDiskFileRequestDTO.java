package com.doudoudrive.common.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * <p>删除es用户文件信息时的请求数据模型</p>
 * <p>2022-05-22 16:20</p>
 *
 * @author Dan
 **/

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteElasticsearchDiskFileRequestDTO {

    /**
     * 用户文件标识，也是es的id值，根据此值删除es数据
     */
    @NotBlank(message = "业务标识不能为空")
    @Size(min = 1, max = 35, message = "业务标识长度错误")
    private String businessId;

}
