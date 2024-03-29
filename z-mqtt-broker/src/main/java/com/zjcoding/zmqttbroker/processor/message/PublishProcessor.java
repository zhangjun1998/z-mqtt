package com.zjcoding.zmqttbroker.processor.message;

import com.zjcoding.zmqttcommon.factory.ZMqttMessageFactory;
import com.zjcoding.zmqttcommon.message.CommonMessage;
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
 * PUBLISH控制包处理器
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
             */
            byte[] payloadBytes = new byte[publishMessage.payload().readableBytes()];
            publishMessage.payload().getBytes(publishMessage.payload().readerIndex(), payloadBytes);

            boolean isDup = publishMessage.fixedHeader().isDup();
            int qos = publishMessage.fixedHeader().qosLevel().value();
            String topic = publishMessage.variableHeader().topicName();
            int messageId = publishMessage.variableHeader().packetId();

            // 处理重复发送的消息
            if (isDup && messageStore.getDump(clientId, messageId) == null) {
                return;
            }

            // 转发消息到订阅者
            if (MqttQoS.EXACTLY_ONCE.value() != qos) {
                forwardPublishMessages(payloadBytes, topic, qos);
            }

            // 存储接收的消息
            CommonMessage receiveMessage = null;

            // qos==1,转发并存储消息，如果一段时间后没有收到接收方的ACK则重发
            if (MqttQoS.AT_LEAST_ONCE.value() == qos) {
                ctx.channel().writeAndFlush(ZMqttMessageFactory.getPubAck(qos, messageId));
            }
            // qos==2,暂不转发消息，先存储并返回REC，收到REL后再转发消息并销毁消息且返回COMP
            if (MqttQoS.EXACTLY_ONCE.value() == qos) {
                receiveMessage = new CommonMessage(topic, qos, payloadBytes, clientId, messageId);
                messageStore.dumpMessage(clientId, receiveMessage);
                ctx.channel().writeAndFlush(ZMqttMessageFactory.getPubRec(qos, messageId));
            }

            // 是否保留接收的消息
            if (publishMessage.fixedHeader().isRetain()) {
                if (receiveMessage == null) {
                    receiveMessage = new CommonMessage(topic, qos, payloadBytes);
                }
                messageStore.storeMessage(topic, receiveMessage);
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
    public void forwardPublishMessages(byte[] payloadBytes, String topic, int qos) {
        // 查找该话题的所有订阅客户端
        List<MqttSubscribe> subscribes = subscribeStore.searchTopic(topic);

        if (!CollectionUtils.isEmpty(subscribes)) {
            int messageId;
            MqttMessage sendMessage;
            int qosMin;
            String clientId;
            CommonMessage forwardMessage;
            for (MqttSubscribe subscribe : subscribes) {
                clientId = subscribe.getClientId();
                // 转发消息到当前在线的客户端
                if (sessionStore.containsKey(clientId)) {
                    qosMin = Math.min(qos, subscribe.getQos());
                    // qos==0时不需要分配消息唯一标识，尽量减少nextId()获取锁占用的时间
                    if (MqttQoS.AT_MOST_ONCE.value() == qosMin) {
                        messageId = 0;
                    } else {
                        messageId = messageUtil.nextId();
                        // 存储转发消息，等待接收方回复确认后释放，未确认则稍后重发
                        forwardMessage = new CommonMessage(topic, qosMin, payloadBytes, clientId, messageId);
                        messageStore.dumpMessage(clientId, forwardMessage);
                    }
                    // 转发消息
                    sendMessage = ZMqttMessageFactory.getPublish(qosMin, topic, payloadBytes, messageId);
                    sessionStore.getSession(clientId).getChannel().writeAndFlush(sendMessage);
                }
            }
        }
    }

}
