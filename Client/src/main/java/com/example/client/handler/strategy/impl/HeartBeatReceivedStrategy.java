package com.example.client.handler.strategy.impl;

import com.example.client.handler.strategy.MessageReceivedStrategy;
import com.example.common.proto.IM_Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HeartBeatReceivedStrategy implements MessageReceivedStrategy {
    @Override
    public void handle(IM_Message message) {
        log.info("收到服务端心跳...");
    }
}
