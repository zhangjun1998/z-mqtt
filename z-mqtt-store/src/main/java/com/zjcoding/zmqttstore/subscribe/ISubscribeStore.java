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
     * @param subscribe:   订阅实体
     * @author ZhangJun
     * @date 11:22 2021/2/27
     */
    void storeSubscribe(String topicFilter, MqttSubscribe subscribe);

    /**
     * 根据客户端标识删除指定主题的订阅信息
     *
     * @param topicFilter: 主题过滤器
     * @param clientId:    客户端标识
     * @author ZhangJun
     * @date 11:24 2021/2/27
     */
    void removeSubscribe(String topicFilter, String clientId);

    /**
     * 根据客户端标识删除订阅
     *
     * @param clientId: 客户端标识
     * @author ZhangJun
     * @date 15:48 2021/3/1
     */
    void removeSubscribe(String clientId);

    /**
     * 搜索主题过滤器对应的订阅者
     *
     * @param topic: 主题过滤器
     * @return java.util.List<com.zjcoding.zmqttcommon.subscribe.MqttSubscribe>
     * @author ZhangJun
     * @date 15:47 2021/3/1
     */
    List<MqttSubscribe> searchTopic(String topic);

}
