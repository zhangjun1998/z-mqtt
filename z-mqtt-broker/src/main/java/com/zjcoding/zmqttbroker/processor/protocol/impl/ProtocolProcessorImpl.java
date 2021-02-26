package com.zjcoding.zmqttbroker.processor.protocol.impl;

import com.zjcoding.zmqttbroker.processor.message.ConnectProcessor;
import com.zjcoding.zmqttbroker.processor.protocol.IProtocolProcessor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttMessage;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * MQTT协议相关实现类
 *
 * @author ZhangJun
 * @date 11:37 2021/2/24
 */

@Component
public class ProtocolProcessorImpl implements IProtocolProcessor {

    @Resource
    private ConnectProcessor connectProcessor;

    @Override
    public void processMqttMessage(ChannelHandlerContext ctx, MqttMessage mqttMessage) {
        switch (mqttMessage.fixedHeader().messageType()) {
            case CONNECT:
                connectProcessor.processConnect(ctx, (MqttConnectMessage) mqttMessage);
                break;
            case CONNACK:
                break;
            case PUBLISH:

                break;
            case PUBACK:
                break;
            case PUBREC:
                break;
            case PUBREL:
                break;
            case PUBCOMP:
                break;
            case SUBSCRIBE:

                break;
            case SUBACK:
                break;
            case UNSUBSCRIBE:

                break;
            case UNSUBACK:
                break;
            case PINGREQ:

                break;
            case PINGRESP:
                break;
            case DISCONNECT:

                break;
            default:
                // ctx.channel().close();
                break;
        }
    }

}
