package com.zjcoding.zmqttstore.message.impl;

import com.zjcoding.zmqttstore.message.IMessageStore;
import io.netty.handler.codec.mqtt.MqttMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 控制包消息存储接口
 *
 * @author ZhangJun
 * @date 15:50 2021/2/26
 */
public class MessageStoreImpl implements IMessageStore {

    private Map<String, List<MqttMessage>> messageMap = new HashMap<>();

    @Override
    public void storeMessage(String topic, MqttMessage mqttMessage) {
        // todo ConcurrentHashMap中存储List也不安全，如何改进并发存储性能
        synchronized (messageMap) {
            // List<MqttMessage> messageList = messageMap.putIfAbsent(new )
        }
    }
}
