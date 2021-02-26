package com.zjcoding.zmqttbroker.processor.protocol;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttMessage;
import org.springframework.stereotype.Component;

/**
 * MQTT协议相关接口
 *
 * @author ZhangJun
 * @date 11:27 2021/2/24
 */
@Component
public interface IProtocolProcessor {

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
