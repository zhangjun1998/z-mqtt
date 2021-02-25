package com.zjcoding.zmqttbroker.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * broker配置文件
 *
 * @author ZhangJun
 * @date 10:16 2021/2/24
 */

@Data
@Component
@ConfigurationProperties(prefix = "spring.z-mqtt.broker")
public class BrokerProperties {

    /**
     * mqtt端口，默认1883
     */
    private int mqttPort = 1883;

    /**
     * 心跳间隔，默认30秒
     */
    private int keepAlive = 30;

    /**
     * 是否开启心跳保活，默认开启
     */
    private boolean soKeepAlive = true;

}
