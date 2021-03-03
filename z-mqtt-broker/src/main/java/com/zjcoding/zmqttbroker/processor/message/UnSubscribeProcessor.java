package com.zjcoding.zmqttbroker.processor.message;

import com.zjcoding.zmqttcommon.factory.ZMqttMessageFactory;
import com.zjcoding.zmqttstore.subscribe.ISubscribeStore;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttUnsubscribeMessage;
import io.netty.util.AttributeKey;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * UNSUBSCRIBE控制包处理器
 *
 * @author ZhangJun
 * @date 16:11 2021/3/1
 */

@Component
public class UnSubscribeProcessor {

    @Resource
    private ISubscribeStore subscribeStore;

    /**
     * UNSUBSCRIBE控制包处理
     *
     * @param ctx:                ChannelHandler上下文
     * @param unsubscribeMessage: UNSUBSCRIBE控制包
     * @author ZhangJun
     * @date 16:14 2021/3/1
     */
    public void processUnSubscribe(ChannelHandlerContext ctx, MqttUnsubscribeMessage unsubscribeMessage) {
        String clientId = ctx.channel().attr(AttributeKey.valueOf("clientid")).toString();
        int messageId = unsubscribeMessage.variableHeader().messageId();
        unsubscribeMessage.payload().topics().forEach((topic) -> {
            subscribeStore.removeSubscribe(topic, clientId);
        });
        ctx.channel().writeAndFlush(ZMqttMessageFactory.getUnSubAck(messageId));
    }

}
