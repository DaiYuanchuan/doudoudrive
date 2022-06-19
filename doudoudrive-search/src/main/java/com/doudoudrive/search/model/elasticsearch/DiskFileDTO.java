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
 * <p>用户文件实体信息ES数据模型</p>
 * <p>2022-05-22 12:53</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Setting(shards = 10, replicas = 0)
@Document(indexName = ConstantConfig.IndexName.DISK_FILE)
public class DiskFileDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 2205221304141542841L;

    /**
     * 自增长标识
     */
    @Field(type = FieldType.Long)
    private Long autoId;

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
     * 文件名
     */
    @Field(type = FieldType.Text, analyzer = ConstantConfig.IkConstant.IK_MAX_WORD, searchAnalyzer = ConstantConfig.IkConstant.IK_MAX_WORD)
    private String fileName;

    /**
     * 文件父级标识
     */
    @Field(type = FieldType.Keyword)
    private String fileParentId;

    /**
     * 文件大小(字节)
     */
    @Field(type = FieldType.Text)
    private String fileSize;

    /**
     * 文件的mime类型
     */
    @Field(type = FieldType.Text)
    private String fileMimeType;

    /**
     * 文件的ETag(资源的唯一标识)
     */
    @Field(type = FieldType.Keyword)
    private String fileEtag;

    /**
     * 是否为文件夹(0:false；1:true)
     */
    @Field(type = FieldType.Boolean)
    private Boolean fileFolder;

    /**
     * 当前文件是否被禁止访问(0:false；1:true)
     */
    @Field(type = FieldType.Boolean)
    private Boolean forbidden;

    /**
     * 当前文件是否被收藏(0:false；1:true)
     */
    @Field(type = FieldType.Boolean)
    private Boolean collect;

    /**
     * 文件当前状态(0:已删除；1:正常)
     */
    @Field(type = FieldType.Text)
    private String status;

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

    /**
     * 表后缀
     */
    @Field(type = FieldType.Text)
    private String tableSuffix;

}
