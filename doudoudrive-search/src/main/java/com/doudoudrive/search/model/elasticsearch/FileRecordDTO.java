package com.doudoudrive.search.model.elasticsearch;

import com.doudoudrive.common.constant.ConstantConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>文件临时操作记录信息ES数据模型</p>
 * <p>2023-07-27 11:40</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Setting(shards = 2, replicas = 0)
@Document(indexName = ConstantConfig.IndexName.FILE_RECORD)
public class FileRecordDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 7805768409616322557L;

    /**
     * 业务标识
     */
    @Id
    @Field(type = FieldType.Keyword)
    private String businessId;

    /**
     * 用户系统内唯一标识
     */
    @Field(type = FieldType.Keyword)
    private String userId;

    /**
     * 文件标识
     */
    @Field(type = FieldType.Keyword)
    private String fileId;

    /**
     * 文件的ETag(资源的唯一标识)
     */
    @Field(type = FieldType.Keyword)
    private String fileEtag;

    /**
     * 动作(0:文件状态；1:文件内容状态；)
     */
    @Field(type = FieldType.Keyword)
    private String action;

    /**
     * 动作类型(action为0:{0:文件被删除}；action为1:{0:待审核；1:待删除}；)
     */
    @Field(type = FieldType.Keyword)
    private String actionType;

    /**
     * 创建时间
     */
    @Field(type = FieldType.Date)
    private Date createTime;

    /**
     * 更新时间
     */
    @Field(type = FieldType.Date)
    private Date updateTime;
}
