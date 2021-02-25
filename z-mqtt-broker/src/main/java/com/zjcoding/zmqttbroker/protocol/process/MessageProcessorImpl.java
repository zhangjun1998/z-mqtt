package com.zjcoding.zmqttbroker.protocol.process;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttMessage;

/**
 * 消息处理接口实现类
 *
 * @author ZhangJun
 * @date 11:37 2021/2/24
 */
public class MessageProcessorImpl implements IMessageProcessor{

    @Override
    public void processMqttMessage(ChannelHandlerContext ctx, MqttMessage mqttMessage) {

    }

}
