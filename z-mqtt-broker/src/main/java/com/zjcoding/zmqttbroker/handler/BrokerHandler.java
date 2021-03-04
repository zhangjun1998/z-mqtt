package com.zjcoding.zmqttbroker.handler;

import com.zjcoding.zmqttbroker.processor.protocol.IProtocolProcessor;
import com.zjcoding.zmqttcommon.factory.ZMqttMessageFactory;
import com.zjcoding.zmqttcommon.session.MqttSession;
import com.zjcoding.zmqttcommon.subscribe.MqttSubscribe;
import com.zjcoding.zmqttstore.session.ISessionStore;
import com.zjcoding.zmqttstore.subscribe.ISubscribeStore;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.util.AttributeKey;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * broker消息处理器
 *
 * @author ZhangJun
 * @date 10:40 2021/2/24
 */

@ChannelHandler.Sharable
@Component
public class BrokerHandler extends SimpleChannelInboundHandler<MqttMessage> {

    @Resource
    private IProtocolProcessor protocol;

    @Resource
    private ISessionStore sessionStore;

    @Resource
    private ISubscribeStore subscribeStore;

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
        System.out.println("客户端连接异常：" + cause);
        // 客户端连接中断，发布遗嘱
        String clientId = ctx.channel().attr(AttributeKey.valueOf("clientId")).get().toString();
        MqttSession session = sessionStore.getSession(clientId);
        if (session.isHasWill()) {
            String willTopic = session.getWillTopic();
            String willContent = session.getWillContent();
            MqttMessage willMessage = ZMqttMessageFactory.getPublish(0, willTopic, willContent.getBytes(StandardCharsets.UTF_8), 0);

            List<MqttSubscribe> subscribes = subscribeStore.searchTopic(willTopic);
            if (!CollectionUtils.isEmpty(subscribes)) {
                for (MqttSubscribe subscribe : subscribes) {
                    sessionStore.getSession(subscribe.getClientId()).getChannel().writeAndFlush(willMessage);
                }
            }
        }
        ctx.channel().close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MqttMessage mqttMessage) throws Exception {
        protocol.processMqttMessage(channelHandlerContext, mqttMessage);
    }
}
