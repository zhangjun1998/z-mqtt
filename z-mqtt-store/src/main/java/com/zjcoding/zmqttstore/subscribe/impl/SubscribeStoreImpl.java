package com.zjcoding.zmqttstore.subscribe.impl;

import com.zjcoding.zmqttcommon.subscribe.MqttSubscribe;
import com.zjcoding.zmqttcommon.util.TopicUtil;
import com.zjcoding.zmqttstore.subscribe.ISubscribeStore;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
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

    @Resource
    private TopicUtil topicUtil;

    private final Map<String, Map<String, MqttSubscribe>> subScribeMap = new ConcurrentHashMap<>();

    @Override
    public void storeSubscribe(String topicFilter, MqttSubscribe subscribe) {
        subScribeMap.computeIfAbsent(topicFilter, (key) -> new ConcurrentHashMap<>());
        subScribeMap.get(topicFilter).put(subscribe.getClientId(), subscribe);
    }

    @Override
    public void removeSubscribe(String topicFilter, String clientId) {
        subScribeMap.get(topicFilter).remove(clientId);
    }

    @Override
    public void removeSubscribe(String clientId) {
        for (Map<String, MqttSubscribe> item : subScribeMap.values()) {
            item.remove(clientId);
        }
    }

    @Override
    public List<MqttSubscribe> searchTopic(String topicFilter) {
        List<MqttSubscribe> subscribes = new ArrayList<>();
        for (String topic : subScribeMap.keySet()) {
            if (topicUtil.matchTopic(topic, topicFilter)) {
                subscribes.addAll(subScribeMap.get(topic).values());
            }
        }
        return subscribes;
    }
}
