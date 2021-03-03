package com.zjcoding.zmqttbroker.processor.message;

import com.zjcoding.zmqttcommon.factory.ZMqttMessageFactory;
import com.zjcoding.zmqttcommon.message.CommonMessage;
import com.zjcoding.zmqttcommon.subscribe.MqttSubscribe;
import com.zjcoding.zmqttcommon.util.MessageUtil;
import com.zjcoding.zmqttcommon.util.TopicUtil;
import com.zjcoding.zmqttstore.message.IMessageStore;
import com.zjcoding.zmqttstore.session.ISessionStore;
import com.zjcoding.zmqttstore.subscribe.ISubscribeStore;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.AttributeKey;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * SUBSCRIBE控制包处理
 *
 * @author ZhangJun
 * @date 10:41 2021/2/27
 */

@Component
public class SubscribeProcessor {

    @Resource
    private ISessionStore sessionStore;

    @Resource
    private IMessageStore messageStore;

    @Resource
    private ISubscribeStore subscribeStore;

    @Resource
    private TopicUtil topicUtil;

    @Resource
    private MessageUtil messageUtil;

    /**
     * SUBSCRIBE控制包处理
     *
     * @param ctx:              ChannelHandler上下文
     * @param subscribeMessage: SUBSCRIBE控制包
     * @author ZhangJun
     * @date 10:45 2021/2/27
     */
    public void processSubscribe(ChannelHandlerContext ctx, MqttSubscribeMessage subscribeMessage) {
        // 检查当前客户端是否已通过连接认证
        String clientId = ctx.channel().attr(AttributeKey.valueOf("clientId")).get().toString();
        if (!sessionStore.containsKey(clientId)) {
            ctx.channel().close();
            return;
        }

        // SUBSCRIBE的固定包头后四位必须为0010，这里没有参考规范处理

        // 控制包唯一标识，SUBACK控制包中需与之对应
        int messageId = subscribeMessage.variableHeader().messageId();

        List<MqttTopicSubscription> subscriptionList = subscribeMessage.payload().topicSubscriptions();
        List<MqttTopicSubscription> checkedSubscriptionList = new ArrayList<>();

        String topicFilter;
        MqttQoS qos;
        List<Integer> grantedQosLevels = new ArrayList<>();

        if (!CollectionUtils.isEmpty(subscriptionList)) {
            // 处理订阅主题
            for (MqttTopicSubscription subscription : subscriptionList) {
                topicFilter = subscription.topicName();
                if (topicUtil.checkTopicFilter(topicFilter)) {
                    checkedSubscriptionList.add(subscription);
                    // 存储订阅信息
                    qos = subscription.qualityOfService();
                    subscribeStore.storeSubscribe(topicFilter, new MqttSubscribe(clientId, topicFilter, qos.value()));
                    grantedQosLevels.add(qos.value());
                } else {
                    grantedQosLevels.add(MqttQoS.FAILURE.value());
                }
            }
            // 返回订阅ACK
            ctx.channel().writeAndFlush(ZMqttMessageFactory.getSubAck(messageId, grantedQosLevels));

            // 向该订阅者发送所有订阅主题下的retain消息
            List<CommonMessage> commonMessages;
            int checkedQos;
            String checkedTopicFilter;
            int retainMessageId;
            for (MqttTopicSubscription subscription : checkedSubscriptionList) {
                checkedQos = subscription.qualityOfService().value();
                checkedTopicFilter = subscription.topicName();
                // 发送该topicFilter下需要返回的retain消息
                commonMessages = messageStore.searchMessages(checkedTopicFilter);
                for (CommonMessage commonMessage : commonMessages) {
                    checkedQos = Math.min(checkedQos, commonMessage.getQos());
                    retainMessageId = messageUtil.nextId(checkedQos != 0);
                    // todo 非池化内存分配是否合理，内存最终是否会被释放
                    ctx.channel().writeAndFlush(ZMqttMessageFactory.getPublish(checkedQos, checkedTopicFilter, Unpooled.buffer().writeBytes(commonMessage.getPayloadBytes()), retainMessageId));
                }
            }
        } else {
            // 非法SUBSCRIBE控制包，直接断开连接
            ctx.channel().close();
        }
    }

}
