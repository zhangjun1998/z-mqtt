package com.zjcoding.zmqttcommon.util;

import org.springframework.stereotype.Component;

/**
 * 控制包相关工具类
 *
 * @author ZhangJun
 * @date 14:39 2021/2/28
 */

@Component
public class MessageUtil {

    /**
     * 有效唯一标识messageId占据两个字节，最大限制为65535，最小限制为1
     */
    private static final int ID_MAX = ~(-1 << 16);

    private volatile int currentId = 0;

    /**
     * 生成消息唯一标识
     * 这种生成方式更加快速，无需释放Id，无需检测Id是否已存在，但不完全保证消息标识的唯一性
     * todo 分布式情况下如何确认不重复
     *
     * @return int
     * @author ZhangJun
     * @date 22:29 2021/3/4
     */
    public synchronized int nextId() {
        currentId ++;
        if (currentId > ID_MAX) {
            currentId = 1;
        }
        return currentId;
    }

}
