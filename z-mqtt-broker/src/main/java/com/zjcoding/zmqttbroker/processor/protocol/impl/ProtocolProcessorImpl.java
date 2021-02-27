package com.zjcoding.zmqttbroker.processor.protocol.impl;

import com.zjcoding.zmqttbroker.processor.message.ConnectProcessor;
import com.zjcoding.zmqttbroker.processor.message.SubscribeProcessor;
import com.zjcoding.zmqttbroker.processor.protocol.IProtocolProcessor;
import com.zjcoding.zmqttcommon.subscribe.MqttSubscribe;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttSubscribeMessage;
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

    @Resource
    private SubscribeProcessor subscribeProcessor;

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
                subscribeProcessor.processSubscribe(ctx, (MqttSubscribeMessage) mqttMessage);
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
