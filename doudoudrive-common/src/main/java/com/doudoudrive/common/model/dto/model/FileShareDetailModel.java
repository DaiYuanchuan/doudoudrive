package com.doudoudrive.common.model.dto.model;

import cn.hutool.core.date.DatePattern;
import com.doudoudrive.common.constant.ConstantConfig;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>网盘文件分享记录详情信息数据模型</p>
 * <p>2022-09-29 20:55</p>
 *
 * @author Dan
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileShareDetailModel implements Serializable {

    @Serial
    private static final long serialVersionUID = 7294695417036387059L;

    /**
     * 文件业务标识
     */
    private String businessId;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件父级标识
     */
    private String fileParentId;

    /**
     * 文件大小(字节)
     */
    private String fileSize;

    /**
     * 文件的mime类型
     */
    private String fileMimeType;

    /**
     * 文件的ETag(资源的唯一标识)
     */
    private String fileEtag;

    /**
     * 是否为文件夹(0:false；1:true)
     */
    private Boolean fileFolder;

    /**
     * 当前文件是否被禁止访问(0:false；1:true)
     */
    private Boolean forbidden;

    /**
     * 文件预览地址
     */
    private String preview;

    /**
     * 文件下载地址
     */
    private String download;

    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DatePattern.NORM_DATETIME_PATTERN, timezone = ConstantConfig.TimeZone.DEFAULT_TIME_ZONE)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DatePattern.NORM_DATETIME_PATTERN, timezone = ConstantConfig.TimeZone.DEFAULT_TIME_ZONE)
    private LocalDateTime updateTime;

    /**
     * 分享记录中文件夹的key值，用于获取文件夹下的文件列表
     */
    private String key;

}
