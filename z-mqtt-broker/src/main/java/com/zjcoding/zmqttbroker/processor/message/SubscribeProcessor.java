package com.zjcoding.zmqttbroker.processor.message;

import com.zjcoding.zmqttcommon.factory.MQTTFactory;
import com.zjcoding.zmqttcommon.subscribe.MqttSubscribe;
import com.zjcoding.zmqttstore.session.ISessionStore;
import com.zjcoding.zmqttstore.subscribe.ISubscribeStore;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttSubscribeMessage;
import io.netty.handler.codec.mqtt.MqttTopicSubscription;
import io.netty.util.AttributeKey;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

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
    private ISubscribeStore subscribeStore;

    /**
     * SUBSCRIBE控制包处理
     *
     * @param ctx: ChannelHandler上下文
     * @param subscribeMessage: SUBSCRIBE控制包
     * @author ZhangJun
     * @date 10:45 2021/2/27
     */
    public void processSubscribe(ChannelHandlerContext ctx, MqttSubscribeMessage subscribeMessage) {
        // 检查当前客户端是否已通过连接认证
        String clientId = ctx.channel().attr(AttributeKey.valueOf("clientId")).get().toString();
        if (!sessionStore.containsKey(clientId)){
            ctx.channel().close();
            return;
        }

        // SUBSCRIBE的固定包头后四位必须为0010，这里没有参考规范处理

        // 控制包唯一标识，SUBACK控制包中需与之对应
        int messageId = subscribeMessage.variableHeader().messageId();

        List<MqttTopicSubscription> subscriptionList = subscribeMessage.payload().topicSubscriptions();
        String topicFilter;
        MqttQoS qos;
        List<Integer> grantedQoSLevels = new ArrayList<>();
        if (!CollectionUtils.isEmpty(subscriptionList)) {
            // 处理订阅主题
            for (MqttTopicSubscription subscription : subscriptionList) {
                topicFilter = subscription.topicName();
                if (checkTopicFilter(topicFilter)) {
                    qos = subscription.qualityOfService();
                    subscribeStore.storeSubscribe(topicFilter, new MqttSubscribe(clientId, topicFilter, qos.value()));
                    grantedQoSLevels.add(qos.value());
                }else {
                    grantedQoSLevels.add(MqttQoS.FAILURE.value());
                }
            }
            // 返回订阅ACK
            ctx.channel().writeAndFlush(MQTTFactory.getSubAck(messageId, grantedQoSLevels));

            // 向该订阅者发送订阅主题下的retain消息

        }else {
            // 非法SUBSCRIBE控制包，直接断开连接
            ctx.channel().close();
        }
    }

    /**
     * 校验主题过滤器是否合法
     *
     * @param topicFilter: 需要进行校验的主题过滤器
     * @return boolean
     * @author ZhangJun
     * @date 12:02 2021/2/27
     */
    private boolean checkTopicFilter(String topicFilter) {
        // 1. 校验层级分隔符，不允许以/符号结尾
        if (StringUtils.endsWithIgnoreCase(topicFilter, "/")) {
            return false;
        }

        // 2. 校验单层通配符


        // 3. 校验多层通配符


        return true;
    }

}
