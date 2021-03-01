package com.zjcoding.zmqttcommon.message;

import lombok.Data;

import java.io.Serializable;

/**
 * Retain消息、遗嘱
 *
 * @author ZhangJun
 * @date 21:29 2021/2/28
 */

@Data
public class RetainMessage implements Serializable {

    /**
     * 消息主题
     */
    private String topic;

    /**
     * 服务质量
     */
    private int qos;

    /**
     * 载荷
     */
    private byte[] payloadBytes;

    /**
     * 标识发布遗嘱的clientId，仅用于存储遗嘱消息
     */
    private String clientId;

    public RetainMessage(String topic, int qos, byte[] payloadBytes) {
        this.topic = topic;
        this.qos = qos;
        this.payloadBytes = payloadBytes;
    }

    public RetainMessage(String topic, int qos, byte[] payloadBytes, String clientId) {
        this.topic = topic;
        this.qos = qos;
        this.payloadBytes = payloadBytes;
        this.clientId = clientId;
    }
}
