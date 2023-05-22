package com.example.service.utils;

import com.example.common.model.RIMUserInfo;
import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserChannelFactory {
    private static final Map<Long, Channel> CHANNEL_MAP = new ConcurrentHashMap<>(16);
    private static final Map<Long, String> SESSION_MAP = new ConcurrentHashMap<>(16);

    public static void saveSession(Long userId,String userName){
        SESSION_MAP.put(userId, userName);
    }

    public static void removeSession(Long userId){
        SESSION_MAP.remove(userId) ;
    }

    /**
     * Save the relationship between the userId and the channel.
     * @param id
     * @param socketChannel
     */
    public static void put(Long id, Channel socketChannel) {
        CHANNEL_MAP.put(id, socketChannel);
    }

    public static Channel get(Long id) {
        return CHANNEL_MAP.get(id);
    }

    public static Map<Long, Channel> getRelationShip() {
        return CHANNEL_MAP;
    }

    public static void remove(Channel nioSocketChannel) {
        CHANNEL_MAP.entrySet().stream().filter(entry -> entry.getValue() == nioSocketChannel).forEach(entry -> CHANNEL_MAP.remove(entry.getKey()));
    }

    /**
     * 获取注册用户信息
     * @param nioSocketChannel
     * @return
     */
    public static RIMUserInfo getUserId(Channel nioSocketChannel){
        for (Map.Entry<Long, Channel> entry : CHANNEL_MAP.entrySet()) {
            Channel value = entry.getValue();
            if (nioSocketChannel == value){
                Long key = entry.getKey();
                RIMUserInfo info = new RIMUserInfo();
                info.setUID(key);
                return info ;
            }
        }
        return null;
    }
}
