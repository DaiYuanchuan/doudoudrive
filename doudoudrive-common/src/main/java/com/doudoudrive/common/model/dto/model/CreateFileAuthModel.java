package com.doudoudrive.common.model.dto.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;

/**
 * <p>创建文件时的鉴权参数模型</p>
 * <p>2022-05-25 18:38</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateFileAuthModel implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户系统内唯一标识
     */
    @NotBlank(message = "用户标识不能为空")
    @Size(max = 35, message = "用户标识长度错误")
    private String userId;

    /**
     * 文件名称
     */
    @NotBlank(message = "请输入文件名称")
    @Size(max = 80, message = "文件夹名称长度错误")
    private String name;

    /**
     * 文件父级标识
     */
    @NotBlank(message = "父级文件夹不能为空")
    @Size(max = 35, message = "未找到指定文件夹")
    private String fileParentId;

    /**
     * 文件大小(字节)
     */
    @NotBlank(message = "文件大小不能为空")
    @Size(max = 20, message = "文件过大")
    private String fileSize;

    /**
     * 文件的mime类型
     */
    @NotBlank(message = "mime类型不能为空")
    @Size(max = 100, message = "mime类型数据过长")
    private String fileMimeType;

    /**
     * 文件的ETag(资源的唯一标识)
     */
    @NotBlank(message = "etag不能为空")
    @Size(max = 50, message = "etag数据过长")
    private String fileEtag;

}
