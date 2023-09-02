package com.example.client.thread.duplicate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j(topic = "ReceivedMessage")
@Component
public class ReceivedMessage {
    Map<Long, Long> P2P = new ConcurrentHashMap<>();

    Map<Long, Long> GROUP = new ConcurrentHashMap<>();

    @Value("${message.expire-time}")
    long expire_time;

    public void putP2PReceivedMessage(long MID, long currentTime) {
        P2P.put(MID, currentTime);
    }

    public void putGroupReceivedMessage(long MID, long currentTime) {
        GROUP.put(MID, currentTime);
    }

    public boolean isP2PMessageExist(long MID) {
        return P2P.containsKey(MID);
    }

    public boolean isGroupMessageExist(long MID) {
        return GROUP.containsKey(MID);
    }

    public void checkDuplicate() {
        long currentTime = System.currentTimeMillis();
        for (Map.Entry entry : P2P.entrySet()) {
            long msgId = (long) entry.getKey();
            long addTime = (long) entry.getValue();
            if (currentTime - addTime >= expire_time * 1000) {
                P2P.remove(msgId);
            }
        }
        for (Map.Entry entry : GROUP.entrySet()) {
            long msgId = (long) entry.getKey();
            long addTime = (long) entry.getValue();
            if (currentTime - addTime >= expire_time * 1000) {
                GROUP.remove(msgId);
            }
        }
        log.info("清除完毕！");
    }

    public void clear() {
        P2P.clear();
        GROUP.clear();
    }
}
