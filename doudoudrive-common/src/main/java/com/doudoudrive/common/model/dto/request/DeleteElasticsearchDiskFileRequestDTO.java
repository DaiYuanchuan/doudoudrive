package com.doudoudrive.common.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

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
    @NotEmpty(message = "参数集合为空")
    @Size(max = 1000, message = "请不要一次性操作太多数据~")
    private List<String> businessId;

}
