package com.doudoudrive.common.model.dto.request;

import cn.hutool.core.date.DatePattern;
import com.doudoudrive.common.constant.ConstantConfig;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * <p>更新es用户文件信息时的请求数据模型</p>
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
    private String fileName;

    /**
     * 文件父级标识
     */
    private String fileParentId;

    /**
     * 当前文件是否被禁止访问(0:false；1:true)
     */
    private Boolean forbidden;

    /**
     * 当前文件是否被收藏(0:false；1:true)
     */
    private Boolean collect;

    /**
     * 文件当前状态(0:已删除；1:正常)
     */
    private String status;

    /**
     * 更新时间
     */
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DatePattern.NORM_DATETIME_PATTERN, timezone = ConstantConfig.TimeZone.DEFAULT_TIME_ZONE)
    private Date updateTime;
}
