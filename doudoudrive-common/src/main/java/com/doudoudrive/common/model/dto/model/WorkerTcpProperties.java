package com.doudoudrive.common.model.dto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Optional;

/**
 * <p>日志工蜂模块tcp通信参数配置</p>
 * <p>2022-12-08 23:22</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "worker.tcp")
public class WorkerTcpProperties {

    /**
     * tcp通信时的url
     */
    private String url;

    /**
     * 是否启用tcp通信
     */
    private Boolean enable;

    /**
     * 是否启用tcp通信
     *
     * @return {@link Boolean} 默认为false
     */
    public Boolean getEnable() {
        return Optional.ofNullable(enable).orElse(Boolean.FALSE);
    }
}
