package com.zjcoding.zmqttbroker.processor.message;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import org.springframework.stereotype.Component;

/**
 * PUBCOMP控制包处理器
 *
 * @author ZhangJun
 * @date 23:08 2021/3/3
 */

@Component
public class PubCompProcessor {

    /**
     * 处理PUBCOMP控制包
     *
     * @param ctx:              ChannelHandler处理器
     * @param idVariableHeader: PUBCOMP可变包头
     * @author ZhangJun
     * @date 23:10 2021/3/3
     */
    public void processPubComp(ChannelHandlerContext ctx, MqttMessageIdVariableHeader idVariableHeader) {
        // todo 释放消息
    }

}
