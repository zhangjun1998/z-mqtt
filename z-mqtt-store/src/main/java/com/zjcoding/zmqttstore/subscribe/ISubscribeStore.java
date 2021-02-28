package com.zjcoding.zmqttstore.subscribe;

import com.zjcoding.zmqttcommon.subscribe.MqttSubscribe;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 订阅信息存储接口
 *
 * @author ZhangJun
 * @date 11:15 2021/2/27
 */

@Component
public interface ISubscribeStore {

    /**
     * 订阅数据存储
     *
     * @param topicFilter: 主题过滤器
     * @param subscribe: 订阅实体
     * @author ZhangJun
     * @date 11:22 2021/2/27
     */
    void storeSubscribe(String topicFilter, MqttSubscribe subscribe);

    /**
     * 根据客户端标识删除指定主题的订阅信息
     *
     * @param topicFilter: 主题过滤器
     * @param clientId: 客户端标识
     * @author ZhangJun
     * @date 11:24 2021/2/27
     */
    void removeSubscribe(String topicFilter, String clientId);

    List<MqttSubscribe> searchTopic(String topic);

}
