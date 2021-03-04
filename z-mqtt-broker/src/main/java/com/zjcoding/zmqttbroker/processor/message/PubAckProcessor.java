package com.zjcoding.zmqttbroker.processor.message;

import com.zjcoding.zmqttcommon.util.MessageUtil;
import com.zjcoding.zmqttstore.message.IMessageStore;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttPubAckMessage;
import io.netty.util.AttributeKey;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * PUBACK控制包处理器
 *
 * @author ZhangJun
 * @date 15:38 2021/3/2
 */

@Component
public class PubAckProcessor {

    @Resource
    private MessageUtil messageUtil;

    @Resource
    private IMessageStore messageStore;

    /**
     * 处理PUBACK控制包
     *
     * @param ctx:           ChannelHandler上下文
     * @param pubAckMessage: PUBACK控制包
     * @author ZhangJun
     * @date 15:40 2021/3/2
     */
    public void processPubAck(ChannelHandlerContext ctx, MqttPubAckMessage pubAckMessage) {
        // 释放dump消息
        String clientId = ctx.channel().attr(AttributeKey.valueOf("clientId")).get().toString();
        int messageId = pubAckMessage.variableHeader().messageId();
        messageStore.removeDump(clientId, messageId);
    }

}
