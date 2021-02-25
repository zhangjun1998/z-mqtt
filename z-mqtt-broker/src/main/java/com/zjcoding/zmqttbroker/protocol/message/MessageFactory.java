package com.zjcoding.zmqttbroker.protocol.message;

import io.netty.handler.codec.mqtt.*;

/**
 * 消息工厂
 *
 * @author ZhangJun
 * @date 14:00 2021/2/24
 */
public class MessageFactory {

    /**
     * 根据参数创建指定的CONNACK控制包
     *
     * @param sessionPresent: 是否存在会话状态
     * @param returnCode: 连接返回码
     * @return io.netty.handler.codec.mqtt.MqttMessage
     * @author ZhangJun
     * @date 14:13 2021/2/24
     */
    public static MqttMessage getConnAck(boolean sessionPresent, MqttConnectReturnCode returnCode) {
        return MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                new MqttConnAckVariableHeader(returnCode, sessionPresent),
                null
        );
    }

}
