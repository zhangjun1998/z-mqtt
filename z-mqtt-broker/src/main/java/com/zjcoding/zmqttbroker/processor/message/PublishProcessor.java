package com.zjcoding.zmqttbroker.processor.message;

import com.zjcoding.zmqttcommon.factory.ZMqttMessageFactory;
import com.zjcoding.zmqttcommon.message.RetainMessage;
import com.zjcoding.zmqttcommon.subscribe.MqttSubscribe;
import com.zjcoding.zmqttcommon.util.MessageUtil;
import com.zjcoding.zmqttstore.message.IMessageStore;
import com.zjcoding.zmqttstore.session.ISessionStore;
import com.zjcoding.zmqttstore.subscribe.ISubscribeStore;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.util.AttributeKey;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

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

    @Resource
    private ISubscribeStore subscribeStore;

    @Resource
    private MessageUtil messageUtil;

    /**
     * PUBLISH控制包处理
     *
     * @param ctx:            ChannelHandler上下文
     * @param publishMessage: PUBLISH控制包
     * @author ZhangJun
     * @date 10:44 2021/2/27
     */
    public void processPublish(ChannelHandlerContext ctx, MqttPublishMessage publishMessage) {

        String clientId = ctx.channel().attr(AttributeKey.valueOf("clientId")).get().toString();

        // 判断该连接是否已通过认证
        if (sessionStore.containsKey(clientId)) {
            // 转发消息到client，根据topic和在线client过滤
            forwardPublishMessages(publishMessage);

            int qoS = publishMessage.fixedHeader().qosLevel().value();
            String topic = publishMessage.variableHeader().topicName();
            int messageId = publishMessage.variableHeader().packetId();

            // 根据qos选择性回复PUBACK/PUBREC
            // qos == 1
            if (MqttQoS.AT_LEAST_ONCE.value() == qoS) {
                ctx.channel().writeAndFlush(ZMqttMessageFactory.getPubAck(qoS, messageId));
            }
            // qos == 2
            if (MqttQoS.EXACTLY_ONCE.value() == qoS) {
                ctx.channel().writeAndFlush(ZMqttMessageFactory.getPubRec(qoS, messageId));
            }

            // 是否保留消息
            if (publishMessage.fixedHeader().isRetain()) {
                RetainMessage retainMessage = new RetainMessage(topic, qoS, getPayloadBuf(publishMessage).array());
                messageStore.storeMessage(topic, retainMessage);
            }
        } else {
            ctx.channel().close();
        }

    }


    /**
     * 转发消息到客户端
     *
     * @param publishMessage: 需要转发的消息
     * @author ZhangJun
     * @date 22:18 2021/2/28
     */
    private void forwardPublishMessages(MqttPublishMessage publishMessage) {
        String topic = publishMessage.variableHeader().topicName();
        int qos = publishMessage.fixedHeader().qosLevel().value();

        // 查找该话题的所有订阅客户端
        List<MqttSubscribe> subscribes = subscribeStore.searchTopic(topic);

        if (!CollectionUtils.isEmpty(subscribes)) {
            int messageId;
            MqttMessage sendMessage;
            for (MqttSubscribe subscribe : subscribes) {
                // 这里每个消息需要创建一个单独的ByteBuf，因为消息发送完会释放ByteBuf
                ByteBuf payload = getPayloadBuf(publishMessage);
                // 转发消息到当前在线的客户端
                if (sessionStore.containsKey(subscribe.getClientId())) {
                    messageId = messageUtil.nextId();
                    sendMessage = ZMqttMessageFactory.getPublish(Math.min(qos, subscribe.getQos()), topic, payload, messageId);
                    sessionStore.getSession(subscribe.getClientId()).getChannel().writeAndFlush(sendMessage);
                }
            }
        }
    }

    /**
     * 获取PUBLISH控制包载荷并返回封装的ByteBuf
     *
     * @param publishMessage:
     * @return io.netty.buffer.ByteBuf
     * @author ZhangJun
     * @date 22:35 2021/2/28
     */
    private ByteBuf getPayloadBuf(MqttPublishMessage publishMessage) {
        ByteBuf payload = Unpooled.buffer();
        byte[] bytes = new byte[publishMessage.payload().readableBytes()];
        payload.writeBytes(publishMessage.payload().getBytes(publishMessage.payload().readerIndex(), bytes));
        return payload;
    }

}
