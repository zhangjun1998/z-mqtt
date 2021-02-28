package com.zjcoding.zmqttcommon.util;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 控制包相关工具类
 *
 * @author ZhangJun
 * @date 14:39 2021/2/28
 */

@Component
public class MessageUtil {

    /**
     * messageId占据两个字节，最大限制为65535
     */
    private static final int ID_MAX = ~(-1 << 16);

    private static final int ID_MIN = 1;;

    private volatile int currentId = ID_MIN;

    private final Map<Integer, Integer> idMap = new HashMap<>();

    /**
     * 生成messageId
     *
     * @return int
     * @author ZhangJun
     * @date 15:01 2021/2/28
     */
    public synchronized int nextId() {
        // todo 这种生成效率慢，而且分布式情况下可能造成Id冲突
        while (idMap.containsKey(currentId)) {
            currentId++;
        }
        idMap.put(currentId, currentId);
        return currentId & ID_MAX;
    }

}