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
 * <p>文件临时操作记录模块实体类</p>
 * <p>2022-05-26 10:50</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileRecord implements Serializable {

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
     * 文件标识
     */
    private String fileId;

    /**
     * 文件的ETag(资源的唯一标识)
     */
    private String fileEtag;

    /**
     * 动作(0:文件状态；1:文件内容状态)
     */
    private String action;

    /**
     * 动作类型(action为0:{0:被删除}；action为1:{0:待审核；1:待删除})
     */
    private String actionType;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

}
