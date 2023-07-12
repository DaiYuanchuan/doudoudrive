package com.doudoudrive.file.model.dto.response;

import cn.hutool.core.date.DatePattern;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.model.dto.model.FileShareDetailModel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>获取匿名分享的文件响应数据模型</p>
 * <p>2022-09-29 19:22</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileShareAnonymousResponseDTO {

    /**
     * 进行分享的用户标识
     */
    private String userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 进行分享的文件名(取每次进行分享的第一个文件名)
     */
    private String shareTitle;

    /**
     * 到期时间，超过该时间则分享失效不可再访问，为空时表示永不过期
     */
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DatePattern.NORM_DATETIME_PATTERN, timezone = ConstantConfig.TimeZone.DEFAULT_TIME_ZONE)
    private LocalDateTime expiration;

    /**
     * 分享链接的创建时间
     */
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DatePattern.NORM_DATETIME_PATTERN, timezone = ConstantConfig.TimeZone.DEFAULT_TIME_ZONE)
    private LocalDateTime createTime;

    /**
     * 搜索结果
     */
    private List<FileShareDetailModel> content;

    /**
     * 下一页的游标值
     */
    private String marker;
}
