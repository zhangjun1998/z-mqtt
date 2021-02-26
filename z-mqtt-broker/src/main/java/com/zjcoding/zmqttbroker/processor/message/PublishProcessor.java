package com.zjcoding.zmqttbroker.processor.message;

import com.zjcoding.zmqttstore.message.IMessageStore;
import com.zjcoding.zmqttstore.session.ISessionStore;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.util.AttributeKey;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * PUBLISH控制包处理
 *
 * @author ZhangJun
 * @date 14:41 2021/2/26
 */

@Component
public class PublishProcessor {

    @Resource
    private ISessionStore sessionStore;

    @Resource
    private IMessageStore messageStore;

    public void processPublish(ChannelHandlerContext ctx, MqttPublishMessage publishMessage) {

        String clentId = ctx.channel().attr(AttributeKey.valueOf("clientId")).get().toString();

        // 判断该连接是否已通过认证
        if (sessionStore.containsKey(clentId)) {
            // 转发消息到client，根据topic和在线client过滤


            // 回复PUBACK/PUBREC
            MqttQoS qoS = publishMessage.fixedHeader().qosLevel();

            // 是否保留消息


        }else {
            ctx.channel().close();
        }

    }




}
