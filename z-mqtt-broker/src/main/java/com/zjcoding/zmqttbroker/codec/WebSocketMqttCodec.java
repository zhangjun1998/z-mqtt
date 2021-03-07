package com.zjcoding.zmqttbroker.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * WebScoket协议与MQTT协议编码转换器
 *
 * @author ZhangJun
 * @date 14:57 2021/3/7
 */

@ChannelHandler.Sharable
@Component
public class WebSocketMqttCodec extends MessageToMessageCodec<BinaryWebSocketFrame, ByteBuf> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        list.add(new BinaryWebSocketFrame(byteBuf.retain()));
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, BinaryWebSocketFrame binaryWebSocketFrame, List<Object> list) throws Exception {
        list.add(binaryWebSocketFrame.retain().content());
    }
}
