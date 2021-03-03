package com.zjcoding.zmqttstore.message.impl;

import com.zjcoding.zmqttcommon.message.CommonMessage;
import com.zjcoding.zmqttcommon.util.TopicUtil;
import com.zjcoding.zmqttstore.message.IMessageStore;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
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

@Component
public class MessageStoreImpl implements IMessageStore {

    @Resource
    private TopicUtil topicUtil;

    /**
     * 存储普通消息
     */
    private final Map<String, CommonMessage> commonMessageMap = new ConcurrentHashMap<>();

    /**
     * 存储dump消息
     */
    private final Map<String, Map<Integer, CommonMessage>> dumpMessageMap = new ConcurrentHashMap<>();

    // todo 如果大量消息一直没有释放会导致内存持续增加，最终OOM

    @Override
    public void storeMessage(String topic, CommonMessage commonMessage) {
        commonMessageMap.put(topic, commonMessage);
    }

    @Override
    public void cleanTopic(String topic) {
        commonMessageMap.remove(topic);
    }

    @Override
    public List<CommonMessage> searchMessages(String topicFilter) {
        List<CommonMessage> messageList = new ArrayList<>();
        for (String topic : commonMessageMap.keySet()) {
            if (topicUtil.matchTopic(topic, topicFilter)) {
                messageList.add(commonMessageMap.get(topic));
            }
        }
        return messageList;
    }

    @Override
    public void dumpMessage(String clientId, CommonMessage dumpMessage) {
        Map<Integer, CommonMessage> messageMap = new HashMap<>();
        messageMap.put(dumpMessage.getMessageId(), dumpMessage);
        dumpMessageMap.get(clientId).putAll(messageMap);
    }

    @Override
    public synchronized void removeDump(String clientId, int messageId) {
        Map<Integer, CommonMessage> messageMap = dumpMessageMap.get(clientId);
        if (messageMap.containsKey(messageId)) {
            messageMap.remove(messageId);
            if (messageMap.size() > 0) {
                dumpMessageMap.put(clientId, messageMap);
            }else {
                dumpMessageMap.remove(clientId);
            }
        }
    }

    @Override
    public void removeDump(String clientId) {
        dumpMessageMap.remove(clientId);
    }

}
