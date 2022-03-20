package com.doudoudrive.search.model.elasticsearch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>用户实体信息ES数据模型</p>
 * <p>2022-03-20 20:02</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Document(indexName = "userinfo")
public class UserInfoDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 2203202013131647778L;

    /**
     * 自增长标识
     */
    @Field(type = FieldType.Long)
    private Long autoId;

    /**
     * 用户系统内唯一标识
     */
    @Id
    @Field(type = FieldType.Keyword)
    private String businessId;

    /**
     * 用户名
     */
    @Field(type = FieldType.Keyword)
    private String userName;

    /**
     * 用户头像
     */
    @Field(type = FieldType.Text)
    private String userAvatar;

    /**
     * 用户邮箱
     */
    @Field(type = FieldType.Keyword)
    private String userEmail;

    /**
     * 用户手机号
     */
    @Field(type = FieldType.Keyword)
    private String userTel;

    /**
     * 用户密码
     */
    @Field(type = FieldType.Text)
    private String userPwd;

    /**
     * 用于登录密码校验的盐值
     */
    @Field(type = FieldType.Text)
    private String userSalt;

    /**
     * 当前账号是否可用(0:false,1:true)
     */
    @Field(type = FieldType.Boolean)
    private Boolean available;

    /**
     * 当前账号不可用原因
     */
    @Field(type = FieldType.Text)
    private String userReason;

    /**
     * 账号被封禁的时间(单位:秒)(-1:永久)最大2144448000
     */
    @Field(type = FieldType.Integer)
    private Integer userBanTime;

    /**
     * 账号解封时间
     */
    @Field(type = FieldType.Date)
    private Date userUnlockTime;

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
