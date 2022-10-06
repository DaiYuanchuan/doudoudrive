package com.doudoudrive.common.model.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>文件分享记录详情实体类</p>
 * <p>2022-09-29 02:21</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileShareDetail implements Serializable {

    @Serial
    private static final long serialVersionUID = 2209290246181664390L;

    /**
     * 自增长标识
     */
    private Long autoId;

    /**
     * 业务标识
     */
    private String businessId;

    /**
     * 用户系统内唯一标识
     */
    private String userId;

    /**
     * 分享的短链接标识
     */
    private String shareId;

    /**
     * 文件标识
     */
    private String fileId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
