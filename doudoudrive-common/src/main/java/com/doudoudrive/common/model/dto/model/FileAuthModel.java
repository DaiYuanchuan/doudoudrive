package com.doudoudrive.common.model.dto.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * <p>文件鉴权参数模型</p>
 * <p>2022-05-24 14:48</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileAuthModel implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 当前资源所属用户id
     */
    private String userId;

    /**
     * 当前资源文件id
     */
    private String fileId;

    /**
     * 分享短链
     */
    private String shareShort;

    /**
     * 短链提取码
     */
    private String code;

    /**
     * 分享时的文件key值
     */
    private String shareKey;

    /**
     * 搜索时的排序值，用作下一页的游标
     */
    private List<Object> sortValues;

    /**
     * 时间戳，记录请求时间，也能保证后续生成的签名不断变换
     */
    private Long timestamp;

}
