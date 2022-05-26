package com.doudoudrive.common.model.dto.model.qiniu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>七牛云CDN加速域名地址相关配置，要有http|https开头</p>
 * <p>2022-05-25 11:55</p>
 *
 * @author Dan
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QiNiuDomainConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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
