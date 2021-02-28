package com.zjcoding.zmqttstore.session;

import com.zjcoding.zmqttcommon.session.MqttSession;
import org.springframework.stereotype.Component;

/**
 * MQTT会话存储接口
 *
 * @author ZhangJun
 * @date 23:35 2021/2/24
 */

@Component
public interface ISessionStore {


    /**
     * MQTT会话存储
     *
     * @param clientId:    客户端标识
     * @param mqttSession: MQTT会话
     * @author ZhangJun
     * @date 23:40 2021/2/24
     */
    void storeSession(String clientId, MqttSession mqttSession);

    /**
     * 根据客户端标识获取相应会话
     *
     * @param clientId: 客户端标识
     * @return com.zjcoding.zmqttcommon.session.MqttSession
     * @author ZhangJun
     * @date 21:55 2021/2/28
     */
    MqttSession getSession(String clientId);

    /**
     * 清除历史会话状态
     *
     * @param clientId: 客户端标识
     * @author ZhangJun
     * @date 23:44 2021/2/24
     */
    void cleanSession(String clientId);

    /**
     * 根据客户端标识查看是否存在该会话
     *
     * @param clientId:
     * @return boolean
     * @author ZhangJun
     * @date 0:22 2021/2/25
     */
    boolean containsKey(String clientId);

}
