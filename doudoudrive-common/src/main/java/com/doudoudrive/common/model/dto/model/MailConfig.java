package com.doudoudrive.common.model.dto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>系统邮件配置对象</p>
 * <p>2022-04-15 11:16</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MailConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 发件服务器地址
     */
    private String host;

    /**
     * 发件服务器端口
     */
    private Integer port;

    /**
     * 发送方，遵循RFC-822标准
     */
    private String from;

    /**
     * 用户名
     */
    private String user;

    /**
     * 邮箱密码
     */
    private String pass;

}