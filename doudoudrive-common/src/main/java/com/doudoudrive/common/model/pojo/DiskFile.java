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
 * <p>用户文件模块实体类</p>
 * <p>2022-05-19 22:44</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiskFile implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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
     * 当前文件是否被收藏(0:false；1:true)
     */
    private Boolean collect;

    /**
     * 文件当前状态(0:已删除；1:正常)
     */
    private String status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
