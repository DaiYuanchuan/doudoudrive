package com.doudoudrive.file.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * <p>获取文件上传token时请求数据模型</p>
 * <p>2022-05-26 15:48</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadTokenRequestDTO {

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
     * 文件的ETag(资源的唯一标识)
     */
    @NotBlank(message = "etag不能为空")
    @Size(max = 50, message = "etag数据过长")
    private String fileEtag;

    /**
     * 回调Url，文件上传成功后的回调地址，对接第三方使用
     */
    private String callbackUrl;

}
