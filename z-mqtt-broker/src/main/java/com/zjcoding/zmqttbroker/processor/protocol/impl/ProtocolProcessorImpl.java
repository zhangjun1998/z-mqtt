package com.zjcoding.zmqttbroker.processor.protocol.impl;

import com.zjcoding.zmqttbroker.processor.message.*;
import com.zjcoding.zmqttbroker.processor.protocol.IProtocolProcessor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.*;
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
    private PublishProcessor publishProcessor;

    @Resource
    private PubAckProcessor pubAckProcessor;

    @Resource
    private PubRecProcessor pubRecProcessor;

    @Resource
    private PubRelProcessor pubRelProcessor;

    @Resource
    private PubCompProcessor pubCompProcessor;

    @Resource
    private SubscribeProcessor subscribeProcessor;

    @Resource
    private UnSubscribeProcessor unSubscribeProcessor;

    @Resource
    private PingReqProcessor pingReqProcessor;

    @Resource
    private DisconnectProcessor disconnectProcessor;

    @Override
    public void processMqttMessage(ChannelHandlerContext ctx, MqttMessage mqttMessage) {
        switch (mqttMessage.fixedHeader().messageType()) {
            case CONNECT:
                connectProcessor.processConnect(ctx, (MqttConnectMessage) mqttMessage);
                break;
            case CONNACK:
                break;
            case PUBLISH:
                publishProcessor.processPublish(ctx, (MqttPublishMessage) mqttMessage);
                break;
            case PUBACK:
                pubAckProcessor.processPubAck(ctx, (MqttPubAckMessage) mqttMessage);
                break;
            case PUBREC:
                pubRecProcessor.processPubRec(ctx, (MqttMessageIdVariableHeader) mqttMessage.variableHeader());
                break;
            case PUBREL:
                pubRelProcessor.processPubRel(ctx, (MqttMessageIdVariableHeader) mqttMessage.variableHeader());
                break;
            case PUBCOMP:
                pubCompProcessor.processPubComp(ctx, (MqttMessageIdVariableHeader) mqttMessage.variableHeader());
                break;
            case SUBSCRIBE:
                subscribeProcessor.processSubscribe(ctx, (MqttSubscribeMessage) mqttMessage);
                break;
            case SUBACK:
                break;
            case UNSUBSCRIBE:
                unSubscribeProcessor.processUnSubscribe(ctx, (MqttUnsubscribeMessage) mqttMessage);
                break;
            case UNSUBACK:
                break;
            case PINGREQ:
                pingReqProcessor.processPingReq(ctx, mqttMessage);
                break;
            case PINGRESP:
                break;
            case DISCONNECT:
                disconnectProcessor.processDisconnect(ctx, mqttMessage);
                break;
            default:
                ctx.channel().close();
                break;
        }
    }

}
