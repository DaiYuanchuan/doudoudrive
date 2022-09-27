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
     * 进行分享的用户标识
     */
    @Field(type = FieldType.Keyword)
    private String userId;

    /**
     * 分享的短链接id
     */
    @Id
    @Field(type = FieldType.Keyword)
    private String shareId;

    /**
     * 进行分享的文件名(取每次进行分享的第一个文件名)
     */
    @Field(type = FieldType.Text)
    private String shareName;

    /**
     * 提取码(为空时表示不需要提取码)
     */
    @Field(type = FieldType.Text)
    private String sharePwd;

    /**
     * 用于计算文件key的盐值
     */
    @Field(type = FieldType.Text)
    private String shareSalt;

    /**
     * 浏览次数，每次分享时都会+1，初始值为0，最大值为9999
     * 超过9999时不再显示，但是可以继续分享和+1
     */
    @Field(type = FieldType.Text)
    private String viewCount;

    /**
     * 保存、转存次数，每次分享时都会+1，初始值为0，最大值为9999
     * 超过9999时不再显示，但是可以继续分享和+1
     */
    @Field(type = FieldType.Text)
    private String saveCount;

    /**
     * 到期时间，超过该时间则分享失效不可再访问，为空时表示永不过期
     */
    @Field(type = FieldType.Date)
    private Date expiration;

    /**
     * 是否已经过期(0:false,1:true)
     */
    @Field(type = FieldType.Boolean)
    private Boolean expired;

    /**
     * 分享的文件中是否包含文件夹(0:false,1:true)
     */
    @Field(type = FieldType.Boolean)
    private Boolean folder;

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
