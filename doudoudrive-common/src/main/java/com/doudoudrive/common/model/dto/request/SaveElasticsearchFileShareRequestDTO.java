package com.doudoudrive.common.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

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
     * 自增长标识
     */
    @NotNull(message = "自增长标识不能为空")
    @Min(value = 0, message = "自增长标识不能为负")
    @Max(value = 9223372036854775807L, message = "自增长标识超出最大值")
    private Long autoId;

    /**
     * 文件分享信息业务标识
     */
    @NotBlank(message = "业务标识不能为空")
    @Size(min = 1, max = 35, message = "业务标识长度错误")
    private String businessId;

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
}
