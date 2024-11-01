package com.doudoudrive.common.model.dto.model.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>CDN加速域名地址相关配置，要有http|https开头</p>
 * <p>2024-04-22 23:08</p>
 *
 * @author Dan
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DomainConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = -3765486436137411517L;

    /**
     * 图片小文件
     */
    private String picture;

    /**
     * 文件下载
     */
    private String download;

    /**
     * 视频流文件
     */
    private String stream;
}
