package com.zjcoding.zmqttcommon.message;

import lombok.Data;

import java.io.Serializable;

/**
 * Retain消息
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

    public RetainMessage(String topic, int qos, byte[] payloadBytes) {
        this.topic = topic;
        this.qos = qos;
        this.payloadBytes = payloadBytes;
    }
}
