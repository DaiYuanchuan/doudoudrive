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
 * <p>用户文件分享记录信息ES数据模型</p>
 * <p>2022-09-24 12:48</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Setting(shards = 2, replicas = 0)
@Document(indexName = ConstantConfig.IndexName.DISK_SHARE_FILE)
public class FileShareDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 2209241530061664004L;

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
     * 进行分享的用户标识
     */
    @Field(type = FieldType.Keyword)
    private String userId;

    /**
     * 分享的短链接id
     */
    @Field(type = FieldType.Keyword)
    private String shareId;

    /**
     * 创建时间
     */
    @Field(type = FieldType.Date)
    private Date createTime;
}
