package com.example.common.enums;

import com.example.common.constant.Constants;

import java.util.HashMap;
import java.util.Map;

public enum MessageSendStrategyEnum {
    P2P(Constants.CommandType.P2P_MSG, "P2PSendStrategy"),
    GROUP(Constants.CommandType.GROUP_MSG, "GroupSendStrategy"),
    ;

    /**
     * 实现类
     */
    private final String clazz;

    /**
     * 消息类型
     */
    private final byte type;


    /**
     * 构造类
     *
     * @param type
     * @param clazz
     */
    MessageSendStrategyEnum(byte type, String clazz) {
        this.type = type;
        this.clazz = clazz;
    }


    /**
     * 获取 class。
     *
     * @return class。
     */
    public String getClazz() {
        return clazz;
    }

    /**
     * 获取type
     *
     * @return
     */
    public byte getType() {
        return type;
    }

    public static Map<Byte, String> getAllClazz() {
        Map<Byte, String> map = new HashMap<Byte, String>(16);
        for (MessageSendStrategyEnum status : values()) {
            map.put(status.getType(), "com.example.route.service.strategy.send.impl." + status.getClazz());
        }
        return map;
    }
}
