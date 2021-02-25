package com.zjcoding.zmqttbroker.security;

import com.zjcoding.zmqttbroker.config.BrokerProperties;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

/**
 * MQTT用户名、密码校验
 *
 * @author ZhangJun
 * @date 22:52 2021/2/24
 */

@Component
public class AuthImpl implements IAuth {

    @Resource
    private BrokerProperties brokerProperties;

    @Override
    public boolean checkAuth(MqttConnectMessage connectMessage) {
        String userName = brokerProperties.getUserName();
        String password = brokerProperties.getPassword();
        String payloadName = "";
        String payloadPass = "";

        if (connectMessage.variableHeader().hasUserName()) {
            payloadName = connectMessage.payload().userName();
        }
        if (connectMessage.variableHeader().hasPassword()) {
            payloadPass = new String(connectMessage.payload().passwordInBytes(), StandardCharsets.UTF_8);
        }

        return userName.equals(payloadName) && password.equals(payloadPass);
    }
}
