package com.zjcoding.zmqttcommon.message;

import lombok.Data;

import java.io.Serializable;

/**
 * 可用于存储Retain消息、遗嘱、dump消息
 *
 * @author ZhangJun
 * @date 21:29 2021/2/28
 */

@Data
public class CommonMessage implements Serializable {

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
     * 标识发布遗嘱的clientId，适用于存储遗嘱消息
     */
    private String clientId;

    /**
     * 消息唯一标识，适用于存储qos等级为1、2的dump消息
     */
    private int messageId;

    public CommonMessage(String topic, int qos, byte[] payloadBytes) {
        this.topic = topic;
        this.qos = qos;
        this.payloadBytes = payloadBytes;
    }

    public CommonMessage(String topic, int qos, byte[] payloadBytes, String clientId) {
        this.topic = topic;
        this.qos = qos;
        this.payloadBytes = payloadBytes;
        this.clientId = clientId;
    }

    public CommonMessage(String topic, int qos, byte[] payloadBytes, String clientId, int messageId) {
        this.topic = topic;
        this.qos = qos;
        this.payloadBytes = payloadBytes;
        this.clientId = clientId;
        this.messageId = messageId;
    }
}
