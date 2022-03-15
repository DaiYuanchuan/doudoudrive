package com.doudoudrive.common.model.dto.model;

/**
 * <p>MQ消费者端的基本配置</p>
 * <p>2022-03-10 23:27</p>
 *
 * @author Dan
 **/
public class MqConfig {

    private Class<?> messageClass;
    private boolean orderlyMessage;

    public Class<?> getMessageClass() {
        return messageClass;
    }

    public void setMessageClass(Class<?> messageClass) {
        this.messageClass = messageClass;
    }

    public boolean isOrderlyMessage() {
        return orderlyMessage;
    }

    public void setOrderlyMessage(boolean orderlyMessage) {
        this.orderlyMessage = orderlyMessage;
    }

}
