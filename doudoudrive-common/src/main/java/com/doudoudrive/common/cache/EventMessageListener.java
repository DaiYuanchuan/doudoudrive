package com.doudoudrive.common.cache;

import com.doudoudrive.common.constant.ConstantConfig;

/**
 * <p>Redis事件消息监听器接口</p>
 * <p>2022-04-23 11:18</p>
 *
 * @author Dan
 **/
public interface EventMessageListener {

    /**
     * 接收到的消息
     *
     * @param key   触发事件的key值
     * @param event 触发的事件类型
     */
    void eventMessage(String key, ConstantConfig.Cache.KeyEventEnum event);

}
