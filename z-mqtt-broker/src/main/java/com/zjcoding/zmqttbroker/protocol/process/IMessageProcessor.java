package com.zjcoding.zmqttbroker.protocol.process;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttMessage;

/**
 * MQTT消息处理接口
 *
 * @author ZhangJun
 * @date 11:27 2021/2/24
 */
public interface IMessageProcessor {

    /**
     * 处理MQTT消息
     *
     * @param ctx: channelHandler上下文
     * @param mqttMessage: 待处理的MQTT消息
     * @author ZhangJun
     * @date 11:33 2021/2/24
     */
    void processMqttMessage(ChannelHandlerContext ctx, MqttMessage mqttMessage);

}
