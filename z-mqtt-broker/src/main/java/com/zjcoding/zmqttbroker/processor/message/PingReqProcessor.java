package com.zjcoding.zmqttbroker.processor.message;

import com.zjcoding.zmqttcommon.factory.ZMqttMessageFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttMessage;
import org.springframework.stereotype.Component;

/**
 * PINGREQ控制包处理
 *
 * @author ZhangJun
 * @date 10:55 2021/3/1
 */

@Component
public class PingReqProcessor {

    /**
     * PINGREQ控制包处理
     *
     * @param ctx:            ChannelHandler上下文
     * @param pingReqMessage: PINGREQ控制包
     * @author ZhangJun
     * @date 11:01 2021/3/1
     */
    public void processPingReq(ChannelHandlerContext ctx, MqttMessage pingReqMessage) {
        ctx.channel().writeAndFlush(ZMqttMessageFactory.getPingResp());
    }

}
