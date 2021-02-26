package com.zjcoding.zmqttbroker.processor.message;

import com.zjcoding.zmqttbroker.processor.MQTTFactory;
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
 * CONNECT控制包处理
 *
 * @author ZhangJun
 * @date 13:48 2021/2/24
 */
@Component
public class ConnectProcessor {

    @Resource
    private IAuth auth;

    @Resource
    private ISessionStore sessionStore;

    public void processConnect(ChannelHandlerContext ctx, MqttConnectMessage connectMessage) {
        if (connectMessage.decoderResult().isFailure()) {
            Throwable throwable = connectMessage.decoderResult().cause();

            if (throwable instanceof MqttUnacceptableProtocolVersionException) {
                ctx.channel().writeAndFlush(MQTTFactory.getConnAck(false, MqttConnectReturnCode.CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION));
            } else if (throwable instanceof MqttIdentifierRejectedException) {
                ctx.channel().writeAndFlush(MQTTFactory.getConnAck(false, MqttConnectReturnCode.CONNECTION_REFUSED_CLIENT_IDENTIFIER_NOT_VALID));
            }

            ctx.channel().close();
            return;
        }

        String clientId = connectMessage.payload().clientIdentifier();
        boolean isCleanSession = connectMessage.variableHeader().isCleanSession();

        // 按照MQTTv3.1.1规范，clientId为空时服务端自动生成一个ClientId，且将cleanSession设置为1
        if (!StringUtils.hasText(clientId)) {
            clientId = UUID.randomUUID().toString();
            isCleanSession = true;
        }

        // 校验用户名、密码
        if (!auth.checkAuth(connectMessage)) {
            ctx.channel().writeAndFlush(MQTTFactory.getConnAck(false, MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD));
            ctx.channel().close();
            return;
        }

        // 处理sessionPresent，清除历史会话状态
        if (isCleanSession) {
            sessionStore.cleanSession(clientId);
        }

        // 处理遗嘱消息
        MqttSession mqttSession = new MqttSession(clientId, isCleanSession, ctx.channel(), false);
        if (connectMessage.variableHeader().isWillFlag()) {
            mqttSession.setHasWill(true);
            mqttSession.setWillTopic(connectMessage.payload().willTopic());
            mqttSession.setWillContent(new String(connectMessage.payload().willMessageInBytes(), StandardCharsets.UTF_8));
            mqttSession.setWillRetain(connectMessage.variableHeader().isWillRetain());
        }

        // 处理keepAlive，达到1.5个心跳周期时断开连接
        if (connectMessage.variableHeader().keepAliveTimeSeconds() > 0) {
            if (ctx.channel().pipeline().names().contains("heartbeat")) {
                ctx.channel().pipeline().remove("heartbeat");
            }
            ctx.pipeline().addFirst("heartbeat", new IdleStateHandler(0, 0, Math.round(connectMessage.variableHeader().keepAliveTimeSeconds() * 1.5f)));
        }

        // 给channel加上clientId作为属性，防止未经授权的连接直接发送控制包
        ctx.channel().attr(AttributeKey.valueOf("clientId")).set(clientId);

        // 返回CONNACK控制包
        boolean sessionPresent = !isCleanSession && sessionStore.containsKey(clientId);
        ctx.channel().writeAndFlush(MQTTFactory.getConnAck(sessionPresent, MqttConnectReturnCode.CONNECTION_ACCEPTED));

        // 当Clean Session为0时，需要恢复会话 todo


    }
}
