package com.doudoudrive.common.model.dto.model;

import com.doudoudrive.common.constant.NumberConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

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
@ConfigurationProperties(prefix = "worker.udp")
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
     * udp最大传输字节长度，超过此长度的需要走tcp通信
     */
    private Integer maxCompressBytes;

    /**
     * 是否启用udp通信
     */
    private Boolean enable;

    /**
     * 是否需要进行长度检查，如果为true，数据包将包含一个长度，以便在接收端进行检查
     *
     * @return {@link Boolean} 默认为false
     */
    public Boolean getLengthCheck() {
        return Optional.ofNullable(lengthCheck).orElse(Boolean.FALSE);
    }

    /**
     * udp发送消息报文最大长度，udp 单个最大报文是 64kb(65536字节)，超过该长度需要采用tcp发送
     *
     * @return {@link Integer} 默认为60000
     */
    public Integer getMaxCompressBytes() {
        return Optional.ofNullable(maxCompressBytes).orElse(NumberConstant.INTEGER_TEN_THOUSAND * NumberConstant.INTEGER_SIX);
    }

    /**
     * 是否启用udp通信
     *
     * @return {@link Boolean} 默认为false
     */
    public Boolean getEnable() {
        return Optional.ofNullable(enable).orElse(Boolean.FALSE);
    }
}
