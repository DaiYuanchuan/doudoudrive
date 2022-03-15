package com.doudoudrive.common.constant;

/**
 * <p>RocketMQ消费者消费状态的枚举</p>
 * <p>2022-03-11 11:50</p>
 *
 * @author Dan
 **/
public enum ConsumeStatusEnum {

    /**
     * 消费成功
     */
    SUCCESS,

    /**
     * 需要重试
     */
    RETRY

}
