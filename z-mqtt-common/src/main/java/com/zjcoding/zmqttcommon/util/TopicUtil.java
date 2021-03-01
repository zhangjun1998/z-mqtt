package com.zjcoding.zmqttcommon.util;

import io.netty.handler.codec.mqtt.MqttMessage;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 主题相关工具
 *
 * @author ZhangJun
 * @date 23:23 2021/2/27
 */

@Component
public class TopicUtil {

    /**
     * 校验主题过滤器是否合法
     *
     * @param topicFilter: 需要进行校验的主题过滤器
     * @return boolean
     * @author ZhangJun
     * @date 12:02 2021/2/27
     */
    public boolean checkTopicFilter(String topicFilter) {
        // 1. 校验层级分隔符，不允许以/符号结尾
        if (StringUtils.endsWithIgnoreCase(topicFilter, "/")) {
            return false;
        }

        // 2. 校验单级通配符，按照协议规范，+符号需要单独占据一个层级
        if (topicFilter.contains("+")) {
            String[] splits = topicFilter.split("/");
            for (String str : splits) {
                if (str.contains("+") && str.length() > 1) {
                    return false;
                }
            }
        }

        // 3. 校验多级通配符，#符号要么单独使用，要么独自存在于最后一个层级
        if (topicFilter.contains("#")) {
            if (StringUtils.countOccurrencesOf(topicFilter, "#") > 1) {
                return false;
            }
            return StringUtils.endsWithIgnoreCase(topicFilter, "#");
        }

        return true;
    }

    /**
     * 判断topic与topicFilter是否匹配，topic与topicFilter需要符合协议规范
     *
     * @param topic:       主题
     * @param topicFilter: 主题过滤器
     * @return boolean
     * @author ZhangJun
     * @date 23:57 2021/2/27
     */
    public boolean matchTopic(String topic, String topicFilter) {
        if (topic.contains("+") || topic.contains("#")) {

            String[] topicSpilts = topic.split("/");
            String[] filterSpilts = topicFilter.split("/");

            if (!topic.contains("#") && topicSpilts.length < filterSpilts.length) {
                return false;
            }

            String level;
            for (int i = 0; i < topicSpilts.length; i++) {
                level = topicSpilts[i];
                if (!level.equals(filterSpilts[i]) && !level.equals("+") && !level.equals("#")) {
                    return false;
                }
            }
        } else {
            return topic.equals(topicFilter);
        }
        return true;
    }

}
