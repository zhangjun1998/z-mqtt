package com.zjcoding.zmqttstore.message;

import io.netty.handler.codec.mqtt.MqttMessage;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Retain消息存储接口
 *
 * @author ZhangJun
 * @date 15:47 2021/2/26
 */

@Component
public interface IMessageStore {

    /**
     * 存储控制包
     *
     * @param topic: 控制包所属主题
     * @param mqttMessage: 控制包
     * @author ZhangJun
     * @date 16:00 2021/2/26
     */
    void storeMessage(String topic, MqttMessage mqttMessage);

    /**
     * 清除topic下的所有消息
     *
     * @param topic: 主题
     * @author ZhangJun
     * @date 23:22 2021/2/26
     */
    void cleanTopic(String topic);

    /**
     * 匹配主题过滤器，寻找对应消息
     *
     * @param topicFilter: 主题过滤器
     * @return java.util.List<io.netty.handler.codec.mqtt.MqttMessage>
     * @author ZhangJun
     * @date 0:01 2021/2/28
     */
    List<MqttMessage> searchMessages(String topicFilter);

}
