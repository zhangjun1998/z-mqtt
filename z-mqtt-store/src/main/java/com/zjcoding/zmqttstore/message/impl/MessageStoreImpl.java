package com.zjcoding.zmqttstore.message.impl;

import com.zjcoding.zmqttstore.message.IMessageStore;
import io.netty.handler.codec.mqtt.MqttMessage;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 控制包消息存储接口
 *
 * @author ZhangJun
 * @date 15:50 2021/2/26
 */

@Component
public class MessageStoreImpl implements IMessageStore {

    /**
     * todo 这种存储结构好像不太合适，而且MqttMessage没有实现序列化接口
     */
    private Map<String, MqttMessage> messageMap = new ConcurrentHashMap<>();

    @Override
    public void storeMessage(String topic, MqttMessage mqttMessage) {
        // todo ConcurrentHashMap中存储List也不安全，如何改进并发存储性能
        messageMap.put(topic, mqttMessage);
    }

    @Override
    public void cleanTopic(String topic) {
        messageMap.remove(topic);
    }
}
