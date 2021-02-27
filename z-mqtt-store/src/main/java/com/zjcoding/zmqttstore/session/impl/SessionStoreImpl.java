package com.zjcoding.zmqttstore.session.impl;

import com.zjcoding.zmqttcommon.session.MqttSession;
import com.zjcoding.zmqttstore.session.ISessionStore;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于内存存储MQTT会话
 *
 * @author ZhangJun
 * @date 23:41 2021/2/24
 */

@Component
public class SessionStoreImpl implements ISessionStore {

    private final ConcurrentHashMap<String, MqttSession> sessionMap = new ConcurrentHashMap<>();

    @Override
    public void storeSession(String clientId, MqttSession mqttSession) {
        sessionMap.put(clientId, mqttSession);
    }

    @Override
    public void cleanSession(String clientId) {
        sessionMap.remove(clientId);
    }

    @Override
    public boolean containsKey(String clientId) {
        return sessionMap.containsKey(clientId);
    }
}
