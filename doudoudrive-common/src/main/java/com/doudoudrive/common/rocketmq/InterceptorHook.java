package com.doudoudrive.common.rocketmq;

import com.doudoudrive.common.model.dto.model.MessageContext;
import com.doudoudrive.common.model.dto.model.MessageModel;

/**
 * <p>方法拦截器钩子，在方法执行前后进行拦截</p>
 * <p>2022-03-13 12:25</p>
 *
 * @author Dan
 **/
public interface InterceptorHook {

    /**
     * 方法执行前的拦截
     *
     * @param messageModel   消息构建时通用消息数据模型
     * @param messageContext 方法执行的参数
     */
    void preHandle(MessageModel messageModel, MessageContext messageContext);

    /**
     * 方法执行后的拦截
     *
     * @param methodSuccess  方法是否回调成功
     * @param messageModel   消息构建时通用消息数据模型
     * @param messageContext 方法执行的参数
     */
    void nextHandle(boolean methodSuccess, MessageModel messageModel, MessageContext messageContext);

}
