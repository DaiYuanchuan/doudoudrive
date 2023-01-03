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
 * <p>网盘文件分享记录信息数据模型</p>
 * <p>2022-09-27 21:07</p>
 *
 * @author Dan
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileShareModel implements Serializable {

    @Serial
    private static final long serialVersionUID = 8664575644790011101L;

    /**
     * 业务标识
     */
    private String businessId;

    /**
     * 进行分享的用户标识
     */
    private String userId;

    /**
     * 分享的短链接id
     */
    private String shareId;

    /**
     * 进行分享的文件名(取每次进行分享的第一个文件名)
     */
    private String shareTitle;

    /**
     * 提取码(为空时表示不需要提取码)
     */
    private String sharePwd;

    /**
     * 用于计算文件key的盐值
     */
    private String shareSalt;

    /**
     * 分享的文件数量
     */
    private Long fileCount;

    /**
     * 浏览次数，每次分享时都会+1，初始值为0，最大值为9999
     * 超过9999时不再显示，但是可以继续分享和+1
     */
    private Long browseCount;

    /**
     * 保存、转存次数，每次分享时都会+1，初始值为0，最大值为9999
     * 超过9999时不再显示，但是可以继续分享和+1
     */
    private Long saveCount;

    /**
     * 下载次数，每次下载时都会+1，初始值为0，最大值为9999
     * 超过9999时不再显示，但是可以继续下载和+1
     */
    private Long downloadCount;

    /**
     * 到期时间，超过该时间则分享失效不可再访问，为空时表示永不过期
     */
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DatePattern.NORM_DATETIME_PATTERN, timezone = ConstantConfig.TimeZone.DEFAULT_TIME_ZONE)
    private LocalDateTime expiration;

    /**
     * 是否已经过期(0:false,1:true)
     */
    private Boolean expired;

    /**
     * 分享的文件中是否包含文件夹(0:false,1:true)
     */
    private Boolean folder;

    /**
     * 状态(0:正常；1:关闭)
     */
    private String status;

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

}
