package com.doudoudrive.file.model.dto.response;

import cn.hutool.core.date.DatePattern;
import com.doudoudrive.common.constant.ConstantConfig;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * <p>网盘文件创建分享链接时的响应数据模型</p>
 * <p>2022-09-29 03:53</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFileShareResponseDTO {

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
     * 到期时间，超过该时间则分享失效不可再访问，为空时表示永不过期
     */
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DatePattern.NORM_DATETIME_PATTERN, timezone = ConstantConfig.TimeZone.DEFAULT_TIME_ZONE)
    private LocalDateTime expiration;

    /**
     * 分享的文件中是否包含文件夹(0:false,1:true)
     */
    private Boolean folder;

}
