package com.doudoudrive.file.model.dto.request;

import com.doudoudrive.common.model.dto.model.OrderByBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * <p>文件搜索请求数据模型</p>
 * <p>2022-06-06 18:29</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileSearchRequestDTO {

    /**
     * 业务标识
     */
    @Size(max = 35, message = "未找到指定文件")
    private String businessId;

    /**
     * 文件名(模糊搜索)
     */
    @Size(max = 80, message = "未找到指定文件")
    private String fileName;

    /**
     * 文件父级标识
     */
    @Size(max = 35, message = "未找到指定文件夹")
    private String fileParentId;

    /**
     * 文件的mime类型
     */
    @Size(max = 100, message = "未找到指定文件")
    private String fileMimeType;

    /**
     * 文件的ETag(资源的唯一标识)
     */
    @Size(max = 50, message = "未找到指定文件")
    private String fileEtag;

    /**
     * 是否为文件夹(0:false；1:true)
     */
    private Boolean fileFolder;

    /**
     * 当前文件是否被收藏(0:false；1:true)
     */
    private Boolean collect;

    /**
     * 上一页游标，为空时默认第一页
     */
    private String marker;

    /**
     * 单次查询的数量、每页大小
     */
    private Integer count;

    /**
     * 排序字段配置
     */
    @Valid
    @NotNull(message = "不支持的排序")
    private OrderByBuilder sort;

}
