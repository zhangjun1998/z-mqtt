package com.zjcoding.zmqttstore.message;

import io.netty.handler.codec.mqtt.MqttMessage;

/**
 * 消息存储接口
 *
 * @author ZhangJun
 * @date 15:47 2021/2/26
 */
public interface IMessageStore {

    /**
     * 存储控制包
     *
     * @param topic: 控制包所属主题
     * @param mqttMessage: 控制包
     * @return void
     * @author ZhangJun
     * @date 16:00 2021/2/26
     */
    void storeMessage(String topic, MqttMessage mqttMessage);

}
