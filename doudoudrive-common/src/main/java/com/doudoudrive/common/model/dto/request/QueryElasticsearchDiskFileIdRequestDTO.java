package com.doudoudrive.common.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * <p>搜索es用户文件Id数据时的请求数据模型</p>
 * <p>2022-08-16 18:05</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryElasticsearchDiskFileIdRequestDTO {

    @NotEmpty(message = "参数集合为空")
    @Size(max = 1000, message = "请不要一次性操作太多数据~")
    private List<String> businessId;

}
