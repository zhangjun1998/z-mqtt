package com.zjcoding.zmqttstore.message.impl;

import com.zjcoding.zmqttcommon.message.RetainMessage;
import com.zjcoding.zmqttcommon.util.TopicUtil;
import com.zjcoding.zmqttstore.message.IMessageStore;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
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

    @Resource
    private TopicUtil topicUtil;

    private final Map<String, RetainMessage> messageMap = new ConcurrentHashMap<>();

    @Override
    public void storeMessage(String topic, RetainMessage retainMessage) {
        messageMap.put(topic, retainMessage);
    }

    @Override
    public void cleanTopic(String topic) {
        messageMap.remove(topic);
    }

    @Override
    public List<RetainMessage> searchMessages(String topicFilter) {
        List<RetainMessage> messageList = new ArrayList<>();
        for (String topic : messageMap.keySet()) {
            if (topicUtil.matchTopic(topic, topicFilter)) {
                messageList.add(messageMap.get(topic));
            }
        }
        return messageList;
    }
}
