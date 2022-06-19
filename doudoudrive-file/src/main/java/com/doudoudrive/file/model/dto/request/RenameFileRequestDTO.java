package com.doudoudrive.file.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * <p>文件重命名时请求数据模型</p>
 * <p>2022-06-18 23:31</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RenameFileRequestDTO {

    /**
     * 需要重命名的文件标识
     */
    @NotBlank(message = "未找到指定文件")
    @Size(max = 35, message = "未找到指定文件")
    private String businessId;

    /**
     * 重命名后的文件名称
     */
    @NotBlank(message = "请输入文件名称")
    @Size(max = 80, message = "文件名称太长")
    private String name;

    /**
     * 获取文件、文件夹名称，文件名称去除前后空格
     *
     * @return 文件名称
     */
    public String getName() {
        return name.trim();
    }

}
