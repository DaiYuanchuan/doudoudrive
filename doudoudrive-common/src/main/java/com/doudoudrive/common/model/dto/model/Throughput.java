package com.doudoudrive.common.model.dto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>短信、邮件每人每天最大吞吐量配置</p>
 * <p>2022-04-27 16:32</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Throughput implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 邮件单人一天内最大发送数量
     */
    private Integer mail;

    /**
     * 短信单人一天内最大发送数量
     */
    private Integer sms;

}
