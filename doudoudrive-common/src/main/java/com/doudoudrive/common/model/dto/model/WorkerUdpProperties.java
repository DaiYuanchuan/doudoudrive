package com.doudoudrive.common.model.dto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

/**
 * <p>日志工蜂模块udp通信参数配置</p>
 * <p>2022-11-14 00:21</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerUdpProperties {

    /**
     * udp通信服务地址
     */
    private String server;

    /**
     * udp通信端口
     */
    private Integer port;

    /**
     * 是否需要进行长度检查，如果为true，数据包将包含一个长度，以便在接收端进行检查
     */
    private Boolean lengthCheck;

    /**
     * 是否需要进行长度检查，如果为true，数据包将包含一个长度，以便在接收端进行检查
     *
     * @return {@link Boolean} 默认为false
     */
    public Boolean getLengthCheck() {
        return Optional.ofNullable(lengthCheck).orElse(Boolean.FALSE);
    }
}
