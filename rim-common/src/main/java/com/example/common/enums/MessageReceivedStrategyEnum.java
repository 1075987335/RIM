package com.example.common.enums;

import com.example.common.constant.Constants;

import java.util.HashMap;
import java.util.Map;

public enum MessageReceivedStrategyEnum {

    P2P(Constants.CommandType.P2P_MSG, "P2PReceivedStrategy"),
    GROUP(Constants.CommandType.GROUP_MSG, "GroupReceivedStrategy"),
    PONG(Constants.CommandType.PONG, "HeartBeatReceivedStrategy"),
    P2PACK(Constants.CommandType.P2P_ACK, "AckReceivedStrategy"),
    GROUPACK(Constants.CommandType.GROUP_ACK, "AckReceivedStrategy")
            ;


    /**
     * 实现类
     */
    private final String clazz ;

    /**
     * 消息类型
     */
    private final byte type;


    /**
     * 构造类
     * @param type
     * @param clazz
     */
    MessageReceivedStrategyEnum(byte type, String clazz) {
        this.type = type;
        this.clazz = clazz ;
    }


    /**
     * 获取 class。
     * @return class。
     */
    public String getClazz() {
        return clazz;
    }

    /**
     * 获取type
     * @return
     */
    public byte getType(){
        return type;
    }

    public static Map<Byte,String> getAllClazz() {
        Map<Byte,String> map = new HashMap<Byte, String>(16) ;
        for (MessageReceivedStrategyEnum status : values()) {
            map.put(status.getType(),"com.example.client.handler.strategy.impl." + status.getClazz()) ;
        }
        return map;
    }
}
