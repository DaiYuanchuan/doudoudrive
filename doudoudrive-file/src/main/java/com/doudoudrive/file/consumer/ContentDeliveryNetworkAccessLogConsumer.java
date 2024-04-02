package com.doudoudrive.file.consumer;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.alibaba.fastjson2.JSONObject;
import com.doudoudrive.common.annotation.RocketmqListener;
import com.doudoudrive.common.annotation.RocketmqTagDistribution;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.model.convert.LogOpInfoConvert;
import com.doudoudrive.common.model.dto.model.MessageContext;
import com.doudoudrive.common.model.dto.model.Region;
import com.doudoudrive.common.model.dto.model.aliyun.AliCloudCdnLogModel;
import com.doudoudrive.common.model.dto.model.auth.CdnAuthModel;
import com.doudoudrive.common.model.dto.model.auth.FileVisitorAuthModel;
import com.doudoudrive.common.model.pojo.LogOp;
import com.doudoudrive.common.util.http.UrlQueryUtil;
import com.doudoudrive.common.util.ip.IpUtils;
import com.doudoudrive.common.util.lang.SpiderUtil;
import com.doudoudrive.commonservice.service.DiskDictionaryService;
import com.doudoudrive.commonservice.service.LogOpService;
import com.doudoudrive.file.manager.DiskUserAttrManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.Future;

/**
 * <p>内容分发网络CDN（Content Delivery Network）访问日志消费者</p>
 * <p>2024-03-31 23:58</p>
 *
 * @author Dan
 **/
@Slf4j
@Component
@RocketmqListener(topic = ConstantConfig.Topic.CDN_ACCESS_LOG_SERVICE, consumerGroup = ConstantConfig.ConsumerGroup.CDN_ACCESS_LOG_CONSUMER_GROUP)
public class ContentDeliveryNetworkAccessLogConsumer {

    /**
     * 用于计算额外的网络流量消耗的比例系数
     * <pre>
     *     日志中response_size字段记录的流量数据，只统计了应用层产生的流量
     *     但实际产生的网络流量（网络层统计的流量）通常比应用层流量要高出7%~15%
     *     主要原因是网络层流量比应用层流量多了以下两个流量消耗：
     *     TCP/IP包头：应用层流量在开始网络传输之前，需要先使用TCP协议（传输层）封装为TCP数据包，再使用IP协议（网络层）封装为IP数据包
     *     TCP重传：由于互联网中网络情况较为复杂，在出现网络拥堵、设备故障等情况下就会出现丢包，通常有3%~10%的数据会被互联网丢弃
     *     基于以上两个额外的网络流量消耗，在行业惯例中，会在基于日志中response_size字段统计出的应用层流量的基础上，再加上7%~15%的网络消耗来得出实际的流量数据
     *     这里取平均值10%做为网络消耗流量，因此实际的流量是通过日志统计流量的1.1倍（即：TCP系数1.1）
     * </pre>
     */
    private static final BigDecimal TCP_COEFFICIENT = new BigDecimal("1.1");
    private LogOpService logOpService;
    private LogOpInfoConvert logOpInfoConvert;
    private DiskUserAttrManager diskUserAttrManager;
    private DiskDictionaryService diskDictionaryService;

    @Autowired
    public void setLogOpService(LogOpService logOpService) {
        this.logOpService = logOpService;
    }

    @Autowired(required = false)
    public void setLogOpInfoConvert(LogOpInfoConvert logOpInfoConvert) {
        this.logOpInfoConvert = logOpInfoConvert;
    }

    @Autowired
    public void setDiskUserAttrManager(DiskUserAttrManager diskUserAttrManager) {
        this.diskUserAttrManager = diskUserAttrManager;
    }

    @Autowired
    public void setDiskDictionaryService(DiskDictionaryService diskDictionaryService) {
        this.diskDictionaryService = diskDictionaryService;
    }

    @RocketmqTagDistribution(messageClass = AliCloudCdnLogModel.class, tag = ConstantConfig.Tag.CDN_ACCESS_LOG_RECORD)
    public void contentDeliveryNetworkLogConsumer(AliCloudCdnLogModel cdnLogModel, MessageContext messageContext) {
        try {
            // 日志信息转换
            LogOp log = logOpConvert(cdnLogModel);

            // 解析鉴权参数
            CdnAuthModel cdnAuthModel = UrlQueryUtil.parse(cdnLogModel.getUriParam(), StandardCharsets.UTF_8, CdnAuthModel.class);
            log.setParameter(JSONObject.toJSONString(cdnAuthModel));
            if (cdnAuthModel != null) {
                // 解密访问者信息，这里的签名与CDN鉴权时的签名一致
                Optional.ofNullable(diskDictionaryService.decrypt(cdnAuthModel.getToken(), FileVisitorAuthModel.class)).ifPresent(visitorInfo -> {
                    log.setUserId(visitorInfo.getUserId());
                    log.setUsername(visitorInfo.getUsername());

                    // 原子性服务增加用户已用流量属性
                    diskUserAttrManager.increase(visitorInfo.getUserId(), ConstantConfig.UserAttrEnum.USED_TRAFFIC, log.getResponseSize(), null);
                });
            }

            logOpService.insert(log);
        } catch (Exception e) {
            log.error("CDN_ACCESS_LOG_RECORD consumer errMsg: {}", e.getMessage(), e);
        }
    }

    /**
     * 日志信息转换，将阿里云CDN日志模型转换为操作日志模型
     *
     * @param cdnLogModel 阿里云CDN日志模型
     * @return 操作日志模型
     */
    private LogOp logOpConvert(AliCloudCdnLogModel cdnLogModel) {
        // 初始化日志信息
        LogOp log = logOpInfoConvert.cdnLogModelConvert(cdnLogModel);

        try {
            // 异步获取IP实际地理位置信息
            Future<Region> future = ThreadUtil.execAsync(() -> IpUtils.getIpLocationByBtree(cdnLogModel.getIp()));

            // 设置请求、响应时间
            Optional.ofNullable(cdnLogModel.getUnixTime()).ifPresent(unixTime -> {
                // 使用atZone()方法将Instant对象转换为指定时区的ZonedDateTime对象
                log.setRequestTime(Instant.ofEpochSecond(unixTime).atZone(ZoneId.systemDefault()).toLocalDateTime());
                if (cdnLogModel.getRequestTime() != null) {
                    log.setResponseTime(log.getRequestTime().plus(cdnLogModel.getRequestTime(), ChronoUnit.MILLIS));
                }
            });

            // 响应状态码为200时，设置成功标识
            Optional.ofNullable(log.getResponseCode()).ifPresent(responseCode -> log.setSuccess(HttpStatus.OK.value() == responseCode));

            // 响应字节大小 * 1.1，结果四舍五入取整数
            if (StringUtils.isNotBlank(cdnLogModel.getResponseSize())) {
                BigDecimal bigDecimal = new BigDecimal(cdnLogModel.getResponseSize()).multiply(TCP_COEFFICIENT);
                log.setResponseSize(bigDecimal.setScale(0, RoundingMode.HALF_UP).toPlainString());
            }

            // 获取/赋值 浏览器、os系统等信息
            assignBrowserInfo(log);
            Region region = future.get();
            log.setLocation(region.getCountry() + ConstantConfig.SpecialSymbols.HYPHEN
                    + region.getProvince() + ConstantConfig.SpecialSymbols.HYPHEN + region.getCity() + StringUtils.SPACE
                    + region.getIsp());
        } catch (Exception e1) {
            log.setErrorCause(e1.getCause().toString());
            log.setErrorMsg(e1.getCause().getMessage());
        }
        return log;
    }

    /**
     * 根据UserAgent解析浏览器、os系统等信息
     *
     * @param log 日志信息
     */
    private void assignBrowserInfo(LogOp log) {
        // 获取/赋值 浏览器、os系统等信息
        if (StringUtils.isNotBlank(log.getUserAgent())) {
            UserAgent ua = UserAgentUtil.parse(log.getUserAgent());
            if (ua != null) {
                log.setBrowser(ua.getBrowser().toString());
                log.setBrowserVersion(ua.getVersion());
                log.setBrowserEngine(ua.getEngine().toString());
                log.setBrowserEngineVersion(ua.getEngineVersion());
                log.setMobile(ua.isMobile());
                log.setOs(ua.getOs().toString());
                log.setPlatform(ua.getPlatform().getName());
            }
            log.setSpider(SpiderUtil.parseSpiderType(log.getUserAgent()));
            log.setUserAgent(log.getUserAgent());
        }
    }
}
