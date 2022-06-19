package com.doudoudrive.common.model.dto.model.qiniu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>七牛云存储区域配置数据模型</p>
 * <p>2022-05-25 12:05</p>
 *
 * @author Dan
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QiNiuRegionConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 区域名称
     * z0 华东
     * z1 华北
     * z2 华南
     * na0 北美
     * as0 东南亚
     * cn-east-2 华东-浙江
     */
    private String region;

    /**
     * 源站直传 使用各个机房对应的域名(多个域名以英文 , 分割)
     */
    private String srcUpHosts;

    /**
     * 加速上传(多个域名以英文 , 分割)
     */
    private String accUpHost;

    /**
     * 源站下载
     */
    private String iovipHost;

    /**
     * 对象管理
     */
    private String rsHost;

    /**
     * 对象列举
     */
    private String rsfHost;

    /**
     * 计量查询
     */
    private String apiHost;

}
