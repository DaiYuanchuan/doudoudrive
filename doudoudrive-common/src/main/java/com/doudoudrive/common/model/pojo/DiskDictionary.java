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
 * <p>数据字典模块实体类</p>
 * <p>2022-04-07 20:10:02</p>
 *
 * @author Dan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiskDictionary implements Serializable {

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
     * 字段名称
     */
    private String dictionaryName;

    /**
     * 字段描述
     */
    private String dictionaryDescribe;

    /**
     * 字段内容
     */
    private String dictionaryContent;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}