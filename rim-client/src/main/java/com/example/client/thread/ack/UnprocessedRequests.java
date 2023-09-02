package com.example.client.thread.ack;

import com.example.client.request.SendMessageVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@Component
public class UnprocessedRequests {
    private Map<Long, SendMessageVo> NORMAL_MESSAGE_UNPROCESS_MAP = new ConcurrentHashMap<>();

    public void put(long messageId, SendMessageVo message) {
        NORMAL_MESSAGE_UNPROCESS_MAP.put(messageId, message);
    }

    public void finish(long messageId) {
        SendMessageVo message = NORMAL_MESSAGE_UNPROCESS_MAP.get(messageId);
        if (message != null) {
            NORMAL_MESSAGE_UNPROCESS_MAP.remove(messageId);
        }
        log.info("消息确认完成，移出AckList...");
    }

    public SendMessageVo get(long messageId) {
        SendMessageVo message = NORMAL_MESSAGE_UNPROCESS_MAP.get(messageId);
        if (message == null) {
            log.info("消息为空！");
            return null;
        }
        return message;
    }

    public int size() {
        return NORMAL_MESSAGE_UNPROCESS_MAP.size();
    }
}
