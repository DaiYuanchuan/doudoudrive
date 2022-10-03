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
 * <p>保存es文件分享记录信息时的请求数据模型</p>
 * <p>2022-09-24 20:55</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveElasticsearchFileShareRequestDTO {

    /**
     * 进行分享的用户标识
     */
    @NotBlank(message = "用户标识不能为空")
    @Size(max = 35, message = "用户标识长度错误")
    private String userId;

    /**
     * 分享的短链接id
     */
    @NotBlank(message = "短链不能为空")
    @Size(max = 35, message = "短链长度错误")
    private String shareId;

    /**
     * 进行分享的文件名(取每次进行分享的第一个文件名)
     */
    @NotBlank(message = "文件名不能为空")
    @Size(max = 100, message = "文件名称长度错误")
    private String shareName;

    /**
     * 提取码(为空时表示不需要提取码)
     */
    @Size(max = 6, message = "请输入6位数字或字母")
    private String sharePwd;

    /**
     * 用于计算文件key的盐值
     */
    @NotBlank(message = "加密盐值不能为空")
    @Size(max = 32, message = "加密盐值长度错误")
    private String salt;

    /**
     * 浏览次数，每次分享时都会+1，初始值为0，最大值为9999
     * 超过9999时不再显示，但是可以继续分享和+1
     */
    @NotNull(message = "浏览次数不能为空")
    @Min(value = 0, message = "浏览次数不能为负")
    @Max(value = 9999L, message = "浏览次数超出最大值")
    private String viewCount;

    /**
     * 保存、转存次数，每次分享时都会+1，初始值为0，最大值为9999
     * 超过9999时不再显示，但是可以继续分享+1
     */
    @NotNull(message = "转存次数不能为空")
    @Min(value = 0, message = "转存次数不能为负")
    @Max(value = 9999L, message = "转存次数超出最大值")
    private String saveCount;

    /**
     * 到期时间，超过该时间则分享失效不可再访问，为空时表示永不过期
     */
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DatePattern.NORM_DATETIME_PATTERN, timezone = ConstantConfig.TimeZone.DEFAULT_TIME_ZONE)
    private Date expiration;

    /**
     * 是否已经过期(0:false,1:true)
     */
    @NotNull(message = "expired不能为空")
    private Boolean expired;

    /**
     * 分享的文件中是否包含文件夹(0:false,1:true)
     */
    @NotNull(message = "folder不能为空")
    private Boolean folder;

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

}
