package com.zjcoding.zmqttbroker.security;

import io.netty.handler.codec.mqtt.MqttConnectMessage;
import org.springframework.stereotype.Component;


/**
 * MQTT安全校验接口
 *
 * @author ZhangJun
 * @date 2021/2/24 22:36
 */

@Component
public interface IAuth {


    /**
     * 校验用户名与密码
     *
     * @param connectMessage: CONNECT控制包
     * @return boolean：校验结果
     * @author ZhangJun
     * @date 22:50 2021/2/24
     */
    boolean checkAuth(MqttConnectMessage connectMessage);

}
