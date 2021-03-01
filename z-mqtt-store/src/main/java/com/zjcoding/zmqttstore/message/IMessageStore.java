package com.zjcoding.zmqttstore.message;

import com.zjcoding.zmqttcommon.message.RetainMessage;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Retain消息存储接口
 *
 * @author ZhangJun
 * @date 15:47 2021/2/26
 */

@Component
public interface IMessageStore {

    /**
     * 存储控制包
     *
     * @param topic:         控制包所属主题
     * @param retainMessage: 需要存储的消息
     * @author ZhangJun
     * @date 16:00 2021/2/26
     */
    void storeMessage(String topic, RetainMessage retainMessage);

    /**
     * 清除topic下的所有消息
     *
     * @param topic: 主题
     * @author ZhangJun
     * @date 23:22 2021/2/26
     */
    void cleanTopic(String topic);

    /**
     * 匹配主题过滤器，寻找对应消息
     *
     * @param topicFilter: 主题过滤器
     * @return java.util.List<com.zjcoding.zmqttcommon.message.RetainMessage>
     * @author ZhangJun
     * @date 21:33 2021/2/28
     */
    List<RetainMessage> searchMessages(String topicFilter);

}
