package com.doudoudrive.common.model.dto.model.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>CDN链接请求回调鉴权数据模型</p>
 * <p>2024-03-31 22:24</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CdnAuthModel {

    /**
     * 请求签名字符串
     */
    private String sign;

    /**
     * 访问用户的认证信息
     */
    private String token;

}
