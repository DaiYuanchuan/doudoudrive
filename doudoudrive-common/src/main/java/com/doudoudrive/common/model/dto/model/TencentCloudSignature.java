package com.doudoudrive.common.model.dto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>腾讯云签名数据模型</p>
 * <p>2022-05-06 15:23</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TencentCloudSignature implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 构建的腾讯云签名信息
     */
    private String signature;

    /**
     * 生成签名时的时间秒数
     */
    private Long timestamp;

}
