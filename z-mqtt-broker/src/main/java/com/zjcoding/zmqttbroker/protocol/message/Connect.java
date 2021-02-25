package com.zjcoding.zmqttbroker.protocol.message;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.UUID;

/**
 * 处理CONNECT控制包
 *
 * @author ZhangJun
 * @date 13:48 2021/2/24
 */
@Component
public class Connect {

    public void processConnect(ChannelHandlerContext ctx, MqttConnectMessage mqttMessage) {
        if (mqttMessage.decoderResult().isFailure()){
            Throwable throwable = mqttMessage.decoderResult().cause();
            if (throwable instanceof MqttUnacceptableProtocolVersionException) {
                ctx.channel().writeAndFlush(MessageFactory.getConnAck(false, MqttConnectReturnCode.CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION));
            }else if (throwable instanceof MqttIdentifierRejectedException) {
                ctx.channel().writeAndFlush(MessageFactory.getConnAck(false, MqttConnectReturnCode.CONNECTION_REFUSED_CLIENT_IDENTIFIER_NOT_VALID));
            }

            ctx.channel().close();
            return;
        }

        String clientId = mqttMessage.payload().clientIdentifier();
        boolean isCleanSession = mqttMessage.variableHeader().isCleanSession();
        String userName;
        String password;

        // 按照MQTTv3.1.1规范，clientId为空时服务端自动生成一个ClientId，且将cleanSession设置为1
        if (!StringUtils.hasText(clientId)){
            clientId = UUID.randomUUID().toString();
            isCleanSession = true;
        }

        if (mqttMessage.variableHeader().hasUserName()) {
            userName = mqttMessage.payload().userName();
        }
        if (mqttMessage.variableHeader().hasPassword()) {
            password = mqttMessage.payload().password();
        }



    }

}
