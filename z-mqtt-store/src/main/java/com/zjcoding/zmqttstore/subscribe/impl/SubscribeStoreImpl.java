package com.zjcoding.zmqttstore.subscribe.impl;

import com.zjcoding.zmqttcommon.subscribe.MqttSubscribe;
import com.zjcoding.zmqttstore.subscribe.ISubscribeStore;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 订阅数据存储
 *
 * @author ZhangJun
 * @date 11:44 2021/2/27
 */

@Component
public class SubscribeStoreImpl implements ISubscribeStore {

    private final Map<String, Map<String, MqttSubscribe>> subScribeMap = new ConcurrentHashMap<>();

    @Override
    public void storeSubscribe(String topicFilter, MqttSubscribe subscribe) {
        subScribeMap.computeIfAbsent(topicFilter, (key) -> new ConcurrentHashMap<>());
        subScribeMap.get(topicFilter).put(subscribe.getClientId(), subscribe);
    }

    @Override
    public void removeSubscribe(String topicFilter, String clientId) {

    }
}
