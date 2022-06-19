package com.doudoudrive.common.model.dto.request;

import cn.hutool.core.date.DatePattern;
import com.doudoudrive.common.constant.ConstantConfig;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.util.Date;

/**
 * <p>保存es用户文件信息时的请求数据模型</p>
 * <p>2022-05-22 14:53</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveElasticsearchDiskFileRequestDTO {

    /**
     * 自增长标识
     */
    @NotNull(message = "自增长标识不能为空")
    @Min(value = 0, message = "自增长标识不能为负")
    @Max(value = 9223372036854775807L, message = "自增长标识超出最大值")
    private Long autoId;

    /**
     * 文件信息业务标识
     */
    @NotBlank(message = "业务标识不能为空")
    @Size(max = 35, message = "业务标识长度错误")
    private String businessId;

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
    @Size(max = 80, message = "文件名称长度错误")
    private String fileName;

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
    private String fileMimeType;

    /**
     * 文件的ETag(资源的唯一标识)，文件夹没有etag
     */
    private String fileEtag;

    /**
     * 是否为文件夹(0:false；1:true)
     */
    @NotNull(message = "文件夹标识不能为空")
    private Boolean fileFolder;

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

    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DatePattern.NORM_DATETIME_PATTERN, timezone = ConstantConfig.TimeZone.DEFAULT_TIME_ZONE)
    private Date createTime;

    /**
     * 更新时间
     */
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DatePattern.NORM_DATETIME_PATTERN, timezone = ConstantConfig.TimeZone.DEFAULT_TIME_ZONE)
    private Date updateTime;

    /**
     * 表后缀
     */
    @NotBlank(message = "表后缀不能为空")
    @Size(min = 1, max = 5, message = "表后缀长度错误")
    private String tableSuffix;

}
