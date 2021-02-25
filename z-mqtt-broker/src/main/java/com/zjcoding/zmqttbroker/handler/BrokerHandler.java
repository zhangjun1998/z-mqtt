package com.zjcoding.zmqttbroker.handler;

import com.zjcoding.zmqttbroker.protocol.process.MessageProcessorImpl;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.MqttMessage;

import javax.annotation.Resource;

/**
 * broker消息处理器
 *
 * @author ZhangJun
 * @date 10:40 2021/2/24
 */
public class BrokerHandler extends SimpleChannelInboundHandler<MqttMessage> {

    @Resource
    private MessageProcessorImpl messageProcessor;

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MqttMessage mqttMessage) throws Exception {
        messageProcessor.processMqttMessage(channelHandlerContext, mqttMessage);
    }
}
