package com.doudoudrive.sms.model.dto;

import com.doudoudrive.common.constant.NumberConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Optional;

/**
 * <p>邮箱、SMS发送数据缓存对象</p>
 * <p>2022-04-25 19:14</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsCache implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 发送的数据
     */
    private String data;

    /**
     * 当前时间偏移1分钟后的时间戳，用来校验在1分钟内是否重复发送
     */
    private Long timestamp;

    /**
     * 缓存数据创建时间的时间戳
     */
    private Long createTime;

    /**
     * 累计发送次数
     */
    private Integer number;

    /**
     * @return 累计发送次数默认为 0
     */
    public Integer getNumber() {
        return Optional.ofNullable(number).orElse(NumberConstant.INTEGER_ZERO);
    }
}
