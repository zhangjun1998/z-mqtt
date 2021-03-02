package com.zjcoding.zmqttbroker.processor.message;

import com.zjcoding.zmqttcommon.factory.ZMqttMessageFactory;
import com.zjcoding.zmqttcommon.message.RetainMessage;
import com.zjcoding.zmqttcommon.subscribe.MqttSubscribe;
import com.zjcoding.zmqttcommon.util.MessageUtil;
import com.zjcoding.zmqttstore.message.IMessageStore;
import com.zjcoding.zmqttstore.session.ISessionStore;
import com.zjcoding.zmqttstore.subscribe.ISubscribeStore;
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
            /*
             * 先获取payload的byte数组，然后每次根据bytes创建ByteBuf,
             * 因为payloa读取之后索引会改变，因此只能读一次，
             * ByteBuf发送后会被释放引用计数，因此每次需要重新创建，
             * todo publishMessage没有被发送，不会自动释放引用计数，使用后需要手动释放，否则会导致堆外内存泄漏？？？
             */
            byte[] payloadBytes = new byte[publishMessage.payload().readableBytes()];
            publishMessage.payload().getBytes(publishMessage.payload().readerIndex(), payloadBytes);

            int qoS = publishMessage.fixedHeader().qosLevel().value();
            String topic = publishMessage.variableHeader().topicName();
            int messageId = publishMessage.variableHeader().packetId();

            forwardPublishMessages(payloadBytes, topic, qoS);

            // 根据qos选择性回复PUBACK或PUBREC
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
                RetainMessage retainMessage = new RetainMessage(topic, qoS, payloadBytes);
                messageStore.storeMessage(topic, retainMessage);
            }
        } else {
            ctx.channel().close();
        }

    }


    /**
     * 转发消息到客户端
     *
     * @param payloadBytes: 消息载荷
     * @param topic:        消息主题
     * @param qos:          服务质量
     * @author ZhangJun
     * @date 23:01 2021/3/1
     */
    private void forwardPublishMessages(byte[] payloadBytes, String topic, int qos) {
        // 查找该话题的所有订阅客户端
        List<MqttSubscribe> subscribes = subscribeStore.searchTopic(topic);

        if (!CollectionUtils.isEmpty(subscribes)) {
            int messageId;
            MqttMessage sendMessage;
            int qosMin;
            for (MqttSubscribe subscribe : subscribes) {
                // 转发消息到当前在线的客户端
                if (sessionStore.containsKey(subscribe.getClientId())) {
                    qosMin = Math.min(qos, subscribe.getQos());
                    messageId = messageUtil.nextId(qosMin != 0);
                    sendMessage = ZMqttMessageFactory.getPublish(qosMin, topic, payloadBytes, messageId);
                    sessionStore.getSession(subscribe.getClientId()).getChannel().writeAndFlush(sendMessage);
                }
            }
        }
    }

}
