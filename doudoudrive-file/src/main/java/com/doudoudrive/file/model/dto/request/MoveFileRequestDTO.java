package com.doudoudrive.file.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * <p>文件移动时请求数据模型</p>
 * <p>2023-01-05 17:47</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoveFileRequestDTO {

    /**
     * 需要移动的文件标识
     */
    @NotEmpty(message = "请选择需要删除的文件")
    @Size(max = 120, message = "请不要一次性操作太多数据~")
    private List<String> businessId;

    /**
     * 需要移动到的目标文件夹标识
     */
    @NotBlank(message = "请选择需要保存到的文件夹")
    @Size(max = 35, message = "未找到指定文件夹")
    private String targetFolderId;

}
