package com.doudoudrive.common.log;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.model.convert.LogOpInfoConvert;
import com.doudoudrive.common.model.dto.model.OpLogInfo;
import com.doudoudrive.common.model.pojo.LogOp;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * <p>操作日志信息处理完成后的实现，用于对消息的持久化处理</p>
 * <p>2022-03-15 13:31</p>
 *
 * @author Dan
 **/
@Component
public class OpLogResultHandlerImpl implements OpLogCompletionHandler {

    /**
     * RocketMQ消息模型
     */
    private RocketMQTemplate rocketmqTemplate;

    private LogOpInfoConvert logOpInfoConvert;

    @Autowired
    public void setRocketmqTemplate(RocketMQTemplate rocketmqTemplate) {
        this.rocketmqTemplate = rocketmqTemplate;
    }

    @Autowired(required = false)
    public void setLogOpInfoConvert(LogOpInfoConvert logOpInfoConvert) {
        this.logOpInfoConvert = logOpInfoConvert;
    }

    /**
     * 异常字段的最大索引值
     */
    private static final Integer MAXIMUM_INDEX = 255;

    /**
     * 操作日志信息处理完成后自动回调该接口
     * 此接口主要用于用户将日志信息存入MySQL、Redis等等
     *
     * @param opLogInfo 处理完成后的 操作日志实体信息
     */
    @Override
    public void complete(OpLogInfo opLogInfo) {
        if (opLogInfo.getIp().equals(ConstantConfig.HttpRequest.IPV6_LOCAL_IP)) {
            opLogInfo.setIp(ConstantConfig.HttpRequest.IPV4_LOCAL_IP);
        }

        // 数据类型转换
        LogOp logOpInfo = logOpInfoConvert.logOpConvert(opLogInfo);

        // 避免 errorCause 为 null
        String errorCause = Optional.ofNullable(logOpInfo.getErrorCause()).orElse(CharSequenceUtil.EMPTY);
        if (errorCause.length() > MAXIMUM_INDEX) {
            logOpInfo.setErrorCause(errorCause.substring(NumberConstant.INTEGER_ZERO, MAXIMUM_INDEX));
        }
        // 避免 errorMsg 为 null
        String errorMsg = Optional.ofNullable(logOpInfo.getErrorMsg()).orElse(CharSequenceUtil.EMPTY);
        if (errorMsg.length() > MAXIMUM_INDEX) {
            logOpInfo.setErrorMsg(errorMsg.substring(NumberConstant.INTEGER_ZERO, MAXIMUM_INDEX));
        }

        // 获取当前的请求体
        HttpServletRequest request = opLogInfo.getRequest();
        // 获取当前请求中的referer字段
        logOpInfo.setReferer(Optional.ofNullable(request.getHeader(ConstantConfig.HttpRequest.REFERER))
                .map(referer -> referer.length() > MAXIMUM_INDEX ? referer.substring(0, MAXIMUM_INDEX) : referer)
                .orElse(CharSequenceUtil.EMPTY));

        // 使用one-way模式发送消息，发送端发送完消息后会立即返回
        String destination = ConstantConfig.Topic.LOG_RECORD + ":" + ConstantConfig.Tag.ACCESS_LOG_RECORD;
        rocketmqTemplate.sendOneWay(destination, ObjectUtil.serialize(logOpInfo));
    }
}
