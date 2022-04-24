package com.doudoudrive.common.cache;

/**
 * <p>Redis消息订阅者分发接口</p>
 * <p>2022-04-10 20:48</p>
 *
 * @author Dan
 **/
public interface RedisMessageSubscriber {

    /**
     * 接收到的消息
     *
     * @param message redis消息体
     * @param channel 当前消息体对应的通道
     */
    void receiveMessage(byte[] message, String channel);

}
