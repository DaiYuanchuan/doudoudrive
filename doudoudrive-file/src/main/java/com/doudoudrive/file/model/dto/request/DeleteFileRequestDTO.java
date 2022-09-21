package com.doudoudrive.file.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * <p>删除文件时请求数据模型</p>
 * <p>2022-08-10 18:43</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteFileRequestDTO {

    @NotEmpty(message = "请选择需要删除的文件")
    @Size(max = 120, message = "请不要一次性操作太多数据~")
    private List<String> businessId;

}
