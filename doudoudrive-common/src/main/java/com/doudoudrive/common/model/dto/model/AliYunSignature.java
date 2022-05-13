package com.doudoudrive.common.model.dto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>阿里云签名数据模型</p>
 * <p>2022-04-29 01:11</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AliYunSignature implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 构造签名的参数
     */
    private String param;

    /**
     * 最终构建的签名数据
     */
    private String signature;

    /**
     * 最终拼接的url地址
     */
    private String requestUrl;
}
