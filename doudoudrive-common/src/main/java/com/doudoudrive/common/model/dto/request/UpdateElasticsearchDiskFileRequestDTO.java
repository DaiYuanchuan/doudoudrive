package com.doudoudrive.common.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * <p>修改es用户文件信息时的请求数据模型</p>
 * <p>2022-05-22 16:22</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateElasticsearchDiskFileRequestDTO {

    /**
     * 文件信息业务标识
     */
    @NotBlank(message = "业务标识不能为空")
    @Size(max = 35, message = "业务标识长度错误")
    private String businessId;

    /**
     * 文件名称
     */
    @NotBlank(message = "请输入文件名称")
    @Size(max = 80, message = "文件名称长度错误")
    private String fileName;

    /**
     * 文件父级标识
     */
    @NotBlank(message = "父级文件夹不能为空")
    @Size(max = 35, message = "未找到指定文件夹")
    private String fileParentId;

    /**
     * 当前文件是否被禁止访问(0:false；1:true)
     */
    @NotNull(message = "被禁止访问标识不能为空")
    private Boolean forbidden;

    /**
     * 当前文件是否被收藏(0:false；1:true)
     */
    @NotNull(message = "被收藏标识不能为空")
    private Boolean collect;

    /**
     * 文件当前状态(0:已删除；1:正常)
     */
    @NotBlank(message = "文件状态不能为空")
    private String status;

}
