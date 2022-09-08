package com.doudoudrive.common.model.dto.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>文件操作记录数据模型</p>
 * <p>2022-09-08 14:52</p>
 *
 * @author Dan
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileRecordModel implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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

}
