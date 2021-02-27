package com.zjcoding.zmqttcommon.subscribe;

import lombok.Data;

import java.io.Serializable;

/**
 * MQTT订阅
 *
 * @author ZhangJun
 * @date 11:16 2021/2/27
 */

@Data
public class MqttSubscribe implements Serializable {

    /**
     * 客户端标识
     */
    private String clientId;

    /**
     * 主题过滤器
     */
    private String topicFilter;

    /**
     * 主题过滤器对应的Qos标识
     */
    private int qos;

    public MqttSubscribe(String clientId, String topicFilter, int qos) {
        this.clientId = clientId;
        this.topicFilter = topicFilter;
        this.qos = qos;
    }
}
