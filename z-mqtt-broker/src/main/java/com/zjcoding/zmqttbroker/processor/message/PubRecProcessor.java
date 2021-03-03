package com.zjcoding.zmqttbroker.processor.message;

import com.zjcoding.zmqttcommon.factory.ZMqttMessageFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttQoS;
import org.springframework.stereotype.Component;

/**
 * PUBREC控制包处理器
 *
 * @author ZhangJun
 * @date 23:00 2021/3/3
 */

@Component
public class PubRecProcessor {

    /**
     * 处理PUBREC控制包
     *
     * @param ctx:
     * @param idVariableHeader:
     * @author ZhangJun
     * @date 23:05 2021/3/3
     */
    public void processPubRec(ChannelHandlerContext ctx, MqttMessageIdVariableHeader idVariableHeader) {
        // todo 释放消息
        ctx.channel().writeAndFlush(ZMqttMessageFactory.getPubRel(MqttQoS.AT_MOST_ONCE.value(), idVariableHeader.messageId()));
    }

}
