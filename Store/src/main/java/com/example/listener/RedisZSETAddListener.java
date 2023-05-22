package com.example.listener;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Data
public class RedisZSETAddListener implements MessageListener {

    private static final PatternTopic topic = new PatternTopic("__keyevent@0__:zadd");
    @Override
    public void onMessage(Message message, byte[] pattern) {
        System.out.println("1111");
        log.info("我被触发了");
        String topic = new String(pattern);
        String msg = new String(message.getBody());
        log.info("收到key添加，主题:{}, 内容:{}", topic, msg);
    }

    public PatternTopic getTopic() {
        return topic;
    }
}
