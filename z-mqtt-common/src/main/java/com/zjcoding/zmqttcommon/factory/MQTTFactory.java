package com.zjcoding.zmqttcommon.factory;

import io.netty.handler.codec.mqtt.*;

import java.util.List;

/**
 * 消息工厂
 *
 * @author ZhangJun
 * @date 14:00 2021/2/24
 */
public class MQTTFactory {

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

    /**
     * 根据参数创建指定的SUBACK控制包
     *
     * @param messageId: 对应的SUBSCRIBE控制包标识
     * @param grantedQoSLevels: 对应订阅主题的Qos集合
     * @return io.netty.handler.codec.mqtt.MqttMessage
     * @author ZhangJun
     * @date 15:00 2021/2/27
     */
    public static MqttMessage getSubAck(int messageId, List<Integer> grantedQoSLevels) {
        return MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.SUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(messageId),
                new MqttSubAckPayload(grantedQoSLevels)
        );
    }

}
