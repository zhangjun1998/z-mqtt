package com.zjcoding.zmqttbroker.processor.message;

import com.zjcoding.zmqttcommon.factory.ZMqttMessageFactory;
import com.zjcoding.zmqttstore.message.IMessageStore;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.util.AttributeKey;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * PUBREC控制包处理器
 *
 * @author ZhangJun
 * @date 23:00 2021/3/3
 */

@Component
public class PubRecProcessor {

    @Resource
    private IMessageStore messageStore;

    /**
     * 处理PUBREC控制包
     *
     * @param ctx:
     * @param idVariableHeader:
     * @author ZhangJun
     * @date 23:05 2021/3/3
     */
    public void processPubRec(ChannelHandlerContext ctx, MqttMessageIdVariableHeader idVariableHeader) {
        String clientId = ctx.channel().attr(AttributeKey.valueOf("clientId")).get().toString();
        int messageId = idVariableHeader.messageId();
        messageStore.removeDump(clientId, messageId);
        ctx.channel().writeAndFlush(ZMqttMessageFactory.getPubRel(MqttQoS.AT_MOST_ONCE.value(), messageId));
    }

}
