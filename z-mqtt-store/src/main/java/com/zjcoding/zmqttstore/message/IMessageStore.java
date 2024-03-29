package com.zjcoding.zmqttstore.message;

import com.zjcoding.zmqttcommon.message.CommonMessage;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

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
     * @param commonMessage: 需要存储的消息
     * @author ZhangJun
     * @date 16:00 2021/2/26
     */
    void storeMessage(String topic, CommonMessage commonMessage);

    /**
     * 清除topic下的所有消息
     *
     * @param topic: 主题
     * @author ZhangJun
     * @date 23:22 2021/2/26
     */
    void cleanTopic(String topic);

    /**
     * 根据clientId清除消息
     *
     * @param clientId: 客户端唯一标识
     * @author ZhangJun
     * @date 16:13 2021/3/7
     */
    void removeMessage(String clientId);

    /**
     * 匹配主题过滤器，寻找对应消息
     *
     * @param topicFilter: 主题过滤器
     * @return java.util.List<com.zjcoding.zmqttcommon.message.RetainMessage>
     * @author ZhangJun
     * @date 21:33 2021/2/28
     */
    List<CommonMessage> searchMessages(String topicFilter);

    /**
     * 存储qos等级为1、2的消息
     *
     * @param clientId: 客户端唯一标识
     * @param dumpMessage: 需要存储的消息
     * @author ZhangJun
     * @date 17:30 2021/3/3
     */
    void dumpMessage(String clientId, CommonMessage dumpMessage);

    /**
     * 查询dump消息
     *
     * @param clientId: 客户端唯一标识
     * @return java.util.Map<java.lang.Integer,com.zjcoding.zmqttcommon.message.CommonMessage>
     * @author ZhangJun
     * @date 16:39 2021/3/7
     */
    Map<Integer, CommonMessage> getDump(String clientId);

    /**
     * 查询dump消息
     *
     * @param clientId: 客户端唯一标识
     * @param messageId: 消息唯一标识
     * @return com.zjcoding.zmqttcommon.message.CommonMessage
     * @author ZhangJun
     * @date 21:55 2021/3/3
     */
    CommonMessage getDump(String clientId, int messageId);

    /**
     * 移除指定消息
     *
     * @param clientId: 客户端唯一标识
     * @param messageId: 消息唯一标识
     * @author ZhangJun
     * @date 17:36 2021/3/3
     */
    void removeDump(String clientId, int messageId);

    /**
     * 根据客户端唯一标识批量移除消息
     *
     * @param clientId: 客户端唯一标识
     * @author ZhangJun
     * @date 17:37 2021/3/3
     */
    void removeDump(String clientId);

}
