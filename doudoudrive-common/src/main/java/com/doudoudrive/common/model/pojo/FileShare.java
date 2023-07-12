package com.doudoudrive.common.model.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>文件分享信息实体类</p>
 * <p>2023-01-03 16:06</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileShare implements Serializable {

    @Serial
    private static final long serialVersionUID = 984365981437844039L;

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
     * 分享的标题(取每次进行分享的第一个文件名)
     */
    private String shareTitle;

    /**
     * 分享链接的提取码
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
     * 浏览次数
     */
    private Long browseCount;

    /**
     * 保存、转存次数
     */
    private Long saveCount;

    /**
     * 下载次数
     */
    private Long downloadCount;

    /**
     * 到期时间
     */
    private LocalDateTime expiration;

    /**
     * 是否已经过期(0:false；1:true)
     */
    private Boolean expired;

    /**
     * 是否包含文件夹(0:false；1:true)
     */
    private Boolean folder;

    /**
     * 状态(0:正常；1:关闭)
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
