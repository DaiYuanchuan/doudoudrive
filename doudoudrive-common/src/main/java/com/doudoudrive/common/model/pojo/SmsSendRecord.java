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
 * <p>SMS发送记录实体类</p>
 * <p>2022-04-15 00:03:25</p>
 *
 * @author Dan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SmsSendRecord implements Serializable {

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
     * 收件人信息
     */
    private String smsRecipient;

    /**
     * 消息发送标题
     */
    private String smsTitle;

    /**
     * 消息发送时的数据标识
     */
    private String smsDataId;

    /**
     * 消息发送失败时获取到的异常原因
     */
    private String smsErrorReason;

    /**
     * 当前操作的用户名
     */
    private String username;

    /**
     * 消息类型(1:邮件；2:短信)
     */
    private String smsType;

    /**
     * 消息发送状态(1:待分发；2:发送成功；3:发送失败)
     */
    private String smsStatus;

    /**
     * 消息发送时间
     */
    private Date smsSendTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}