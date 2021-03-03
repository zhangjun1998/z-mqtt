package com.zjcoding.zmqttbroker.processor.message;

import com.zjcoding.zmqttcommon.session.MqttSession;
import com.zjcoding.zmqttstore.session.ISessionStore;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.util.AttributeKey;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * DISCONNECT控制包处理器
 *
 * @author ZhangJun
 * @date 11:06 2021/3/1
 */

@Component
public class DisconnectProcessor {

    @Resource
    private ISessionStore sessionStore;

    /**
     * DISCONNECT控制包处理
     *
     * @param ctx:               ChannelHandler上下文
     * @param disconnectMessage: DISCONNECT控制包
     * @author ZhangJun
     * @date 11:12 2021/3/1
     */
    public void processDisconnect(ChannelHandlerContext ctx, MqttMessage disconnectMessage) {
        // 丢弃遗嘱
        String clientid = ctx.channel().attr(AttributeKey.valueOf("clientId")).get().toString();
        MqttSession session = sessionStore.getSession(clientid);
        session.setHasWill(false);
        session.setWillTopic(null);
        session.setWillContent(null);
        sessionStore.storeSession(clientid, session);
        // 关闭连接
        ctx.channel().close();
    }

}
