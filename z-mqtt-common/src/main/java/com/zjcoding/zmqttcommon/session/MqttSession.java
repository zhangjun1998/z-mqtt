package com.zjcoding.zmqttcommon.session;

import io.netty.channel.Channel;
import lombok.Data;

import java.io.Serializable;

/**
 * MQTT会话
 *
 * @author ZhangJun
 * @date 23:30 2021/2/24
 */

@Data
public class MqttSession implements Serializable {

    /**
     * 客户端标识
     */
    private String clientId;

    /**
     * 是否清除session
     */
    private boolean cleanSession;

    /**
     * 客户端对应的channel连接
     */
    private Channel channel;

    /**
     * 遗嘱消息相关
     */
    private boolean hasWill;
    private String willTopic;
    private String willContent;
    private boolean willRetain;

    public MqttSession(String clientId, boolean cleanSession, Channel channel, boolean hasWill) {
        this.clientId = clientId;
        this.cleanSession = cleanSession;
        this.channel = channel;
        this.hasWill = hasWill;
    }
}
