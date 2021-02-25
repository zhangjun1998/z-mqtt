package com.zjcoding.zmqttbroker.protocol.processor;

import com.zjcoding.zmqttbroker.protocol.message.Connect;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttMessage;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 消息处理接口实现类
 *
 * @author ZhangJun
 * @date 11:37 2021/2/24
 */

@Component
public class MessageProcessorImpl implements IMessageProcessor{

    @Resource
    private Connect connect;

    @Override
    public void processMqttMessage(ChannelHandlerContext ctx, MqttMessage mqttMessage) {
        switch (mqttMessage.fixedHeader().messageType()) {
            case CONNECT:
                connect.processConnect(ctx, (MqttConnectMessage) mqttMessage);
                break;
            case CONNACK:
                break;
            default:
                // ctx.channel().close();
                break;
        }
    }

}
