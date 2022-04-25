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
 * <p>邮箱、SMS验证码缓存对象</p>
 * <p>2022-04-25 19:14</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationCodeCache implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 验证码信息
     */
    private String securityCode;

    /**
     * 当前时间偏移1分钟后的时间戳，用来校验在1分钟内是否重复发送
     */
    private Long timestamp;

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
