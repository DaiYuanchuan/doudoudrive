package com.doudoudrive.common.model.dto.model;

import lombok.Data;

import java.lang.reflect.Method;
import java.util.*;

/**
 * <p>订阅组实体配置</p>
 * <p>2022-03-10 23:40</p>
 *
 * @author Dan
 **/
@Data
public class SubscriptionGroup {

    private String topic;

    private List<String> tagList;

    private Object target;

    private Map<String, Method> tagMethods;

    public SubscriptionGroup(String topic) {
        this.tagList = new ArrayList<>();
        this.tagMethods = new HashMap<>();
        this.topic = topic;
    }

    public void putTagToGroup(String tag, Method method) {
        if (tagList.contains(tag)) {
            throw new IllegalArgumentException("重复的消费者");
        }
        tagList.add(tag);
        tagMethods.put(tag, method);
    }

    public Method getMethod(String tag) {
        return tagMethods.get(tag);
    }

    public Collection<Method> getAllMethods() {
        return tagMethods.values();
    }

}
