package com.zjcoding.zmqttcommon.factory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.*;

import java.util.List;

/**
 * 消息工厂
 *
 * @author ZhangJun
 * @date 14:00 2021/2/24
 */
public class ZMqttMessageFactory {

    /**
     * 根据参数创建PUBLISH控制包
     *
     * @param qos:       服务质量
     * @param topic:     主题
     * @param payload:   载荷
     * @param messageId: 消息标识
     * @return io.netty.handler.codec.mqtt.MqttMessage
     * @author ZhangJun
     * @date 0:38 2021/2/28
     */
    public static MqttMessage getPublish(int qos, String topic, ByteBuf payload, int messageId) {
        return MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBLISH, false, MqttQoS.valueOf(qos), false, 0),
                new MqttPublishVariableHeader(topic, messageId),
                payload
        );
    }

    public static MqttMessage getPublish(int qos, String topic, byte[] payload, int messageId) {
        ByteBuf byteBuf = Unpooled.buffer().writeBytes(payload);
        return getPublish(qos, topic, byteBuf, messageId);
    }

    /**
     * 根据参数创建PUBACK控制包
     *
     * @param qos:          服务质量
     * @param pubMessageId: PUBLISH消息唯一标识
     * @return io.netty.handler.codec.mqtt.MqttMessage
     * @author ZhangJun
     * @date 22:25 2021/2/28
     */
    public static MqttMessage getPubAck(int qos, int pubMessageId) {
        return MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBACK, false, MqttQoS.valueOf(qos), false, 0),
                MqttMessageIdVariableHeader.from(pubMessageId),
                null
        );
    }

    /**
     * 根据参数创建PUBREC控制包
     *
     * @param qos:          服务质量
     * @param pubMessageId: PUBLISH消息唯一标识
     * @return io.netty.handler.codec.mqtt.MqttMessage
     * @author ZhangJun
     * @date 22:26 2021/2/28
     */
    public static MqttMessage getPubRec(int qos, int pubMessageId) {
        return MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBREC, false, MqttQoS.valueOf(qos), false, 0),
                MqttMessageIdVariableHeader.from(pubMessageId),
                null
        );
    }

    /**
     * 根据参数创建PUBREL控制包
     *
     * @param qos:          服务质量
     * @param pubMessageId: PUBLISH消息唯一标识
     * @return io.netty.handler.codec.mqtt.MqttMessage
     * @author ZhangJun
     * @date 23:03 2021/3/3
     */
    public static MqttMessage getPubRel(int qos, int pubMessageId) {
        return MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBREL, false, MqttQoS.valueOf(qos), false, 0),
                MqttMessageIdVariableHeader.from(pubMessageId),
                null
        );
    }

    /**
     * 根据参数创建PUBCOMP控制包
     *
     * @param qos:          服务质量
     * @param pubMessageId: 消息唯一标识
     * @return io.netty.handler.codec.mqtt.MqttMessage
     * @author ZhangJun
     * @date 22:08 2021/3/4
     */
    public static MqttMessage getPubComp(int qos, int pubMessageId) {
        return MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBCOMP, false, MqttQoS.valueOf(qos), false, 0),
                MqttMessageIdVariableHeader.from(pubMessageId),
                null
        );
    }

    /**
     * 根据参数创建指定的CONNACK控制包
     *
     * @param sessionPresent: 是否存在会话状态
     * @param returnCode:     连接返回码
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
     * @param subMessageId:     对应的SUBSCRIBE控制包标识
     * @param grantedQoSLevels: 对应订阅主题的Qos集合
     * @return io.netty.handler.codec.mqtt.MqttMessage
     * @author ZhangJun
     * @date 15:00 2021/2/27
     */
    public static MqttMessage getSubAck(int subMessageId, List<Integer> grantedQoSLevels) {
        return MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.SUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(subMessageId),
                new MqttSubAckPayload(grantedQoSLevels)
        );
    }

    /**
     * 获取UNSUBACK控制包
     *
     * @param unSubMessageId: 对应的UNSUBSCRIBE控制包唯一标识
     * @return io.netty.handler.codec.mqtt.MqttMessage
     * @author ZhangJun
     * @date 16:19 2021/3/1
     */
    public static MqttMessage getUnSubAck(int unSubMessageId) {
        return MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.UNSUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(unSubMessageId),
                null
        );
    }


    /**
     * 获取PINGRESP控制包
     *
     * @return io.netty.handler.codec.mqtt.MqttMessage
     * @author ZhangJun
     * @date 10:59 2021/3/1
     */
    public static MqttMessage getPingResp() {
        return MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PINGRESP, false, MqttQoS.AT_MOST_ONCE, false, 0),
                null,
                null
        );
    }

}
