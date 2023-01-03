package com.doudoudrive.file.model.dto.request;

import cn.hutool.core.date.DatePattern;
import com.doudoudrive.common.constant.ConstantConfig;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>网盘文件创建分享链接时的请求数据模型</p>
 * <p>2022-09-28 23:21</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFileShareRequestDTO {

    /**
     * 过期时间(需要在当前时间之后)
     */
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DatePattern.NORM_DATETIME_PATTERN, timezone = ConstantConfig.TimeZone.DEFAULT_TIME_ZONE)
    private LocalDateTime expiration;

    /**
     * 提取码(为空时表示不需要提取码)
     */
    @Size(max = 6, message = "请输入6位数字或字母")
    private String sharePwd;

    /**
     * 需要分享的文件标识列表信息
     */
    @NotEmpty(message = "请选择需要分享的文件")
    @Size(max = 120, message = "请不要一次性操作太多数据~")
    private List<String> files;

}
