package com.zjcoding.zmqttbroker.protocol.message;

import com.zjcoding.zmqttbroker.security.IAuth;
import com.zjcoding.zmqttcommon.session.MqttSession;
import com.zjcoding.zmqttstore.session.ISessionStore;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.*;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * 处理CONNECT控制包
 *
 * @author ZhangJun
 * @date 13:48 2021/2/24
 */
@Component
public class Connect {

    @Resource
    private IAuth auth;

    @Resource
    private ISessionStore sessionStore;

    public void processConnect(ChannelHandlerContext ctx, MqttConnectMessage mqttMessage) {
        if (mqttMessage.decoderResult().isFailure()) {
            Throwable throwable = mqttMessage.decoderResult().cause();

            if (throwable instanceof MqttUnacceptableProtocolVersionException) {
                ctx.channel().writeAndFlush(MessageFactory.getConnAck(false, MqttConnectReturnCode.CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION));
            } else if (throwable instanceof MqttIdentifierRejectedException) {
                ctx.channel().writeAndFlush(MessageFactory.getConnAck(false, MqttConnectReturnCode.CONNECTION_REFUSED_CLIENT_IDENTIFIER_NOT_VALID));
            }

            ctx.channel().close();
            return;
        }

        String clientId = mqttMessage.payload().clientIdentifier();
        boolean isCleanSession = mqttMessage.variableHeader().isCleanSession();

        // 按照MQTTv3.1.1规范，clientId为空时服务端自动生成一个ClientId，且将cleanSession设置为1
        if (!StringUtils.hasText(clientId)) {
            clientId = UUID.randomUUID().toString();
            isCleanSession = true;
        }

        // 校验用户名、密码
        if (!auth.checkAuth(mqttMessage)) {
            ctx.channel().writeAndFlush(MessageFactory.getConnAck(false, MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD));
            ctx.channel().close();
            return;
        }

        // 处理sessionPresent，清除历史会话状态
        if (isCleanSession) {
            sessionStore.cleanSession(clientId);
        }

        // 处理遗嘱消息
        MqttSession mqttSession = new MqttSession(clientId, isCleanSession, ctx.channel(), false);
        if (mqttMessage.variableHeader().isWillFlag()) {
            mqttSession.setHasWill(true);
            mqttSession.setWillTopic(mqttMessage.payload().willTopic());
            mqttSession.setWillContent(new String(mqttMessage.payload().willMessageInBytes(), StandardCharsets.UTF_8));
            mqttSession.setWillRetain(mqttMessage.variableHeader().isWillRetain());
        }

        // 处理keepAlive，达到1.5个心跳周期时断开连接
        if (mqttMessage.variableHeader().keepAliveTimeSeconds() > 0) {
            if (ctx.channel().pipeline().names().contains("heartbeat")) {
                ctx.channel().pipeline().remove("heartbeat");
            }
            ctx.pipeline().addFirst("heartbeat", new IdleStateHandler(0, 0, Math.round(mqttMessage.variableHeader().keepAliveTimeSeconds() * 1.5f)));
        }

        // 给channel加上clientId作为属性
        ctx.channel().attr(AttributeKey.valueOf("clientId")).set(clientId);

        // 返回CONNACK控制包
        boolean sessionPresent = !isCleanSession && sessionStore.containsKey(clientId);
        ctx.channel().writeAndFlush(MessageFactory.getConnAck(sessionPresent, MqttConnectReturnCode.CONNECTION_ACCEPTED));

        // 当Clean Session为0时，需要恢复会话 todo


    }

}
