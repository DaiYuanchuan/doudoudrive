package com.doudoudrive.common.model.dto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * <p>文件分享嵌套数据模型</p>
 * <p>2022-10-16 23:30</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileNestedModel {

    /**
     * 源文件标识对应的key值
     */
    @NotBlank(message = "文件key值不能为空")
    private String key;

    /**
     * 需要进行复制的源文件标识
     */
    @NotBlank(message = "文件标识不能为空")
    @Size(max = 35, message = "文件标识长度错误")
    private String fileId;

}
