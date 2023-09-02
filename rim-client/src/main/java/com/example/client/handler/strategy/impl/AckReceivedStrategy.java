package com.example.client.handler.strategy.impl;

import com.example.client.handler.strategy.MessageReceivedStrategy;
import com.example.client.thread.ack.UnprocessedRequests;
import com.example.common.proto.Header;
import com.example.common.proto.IM_Message;
import com.example.common.util.RingBufferWheel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AckReceivedStrategy implements MessageReceivedStrategy {

    @Autowired
    UnprocessedRequests unprocessedRequests;

    @Autowired
    RingBufferWheel outTimeJobExecutor;

    @Override
    public void handle(IM_Message message) {
        Header header = message.getHeader();
        log.info("收到消息[{}]的{}类型的ACK", header.getMID(), header.getType());
        //在时间轮中取消任务
        outTimeJobExecutor.cancel(header.getMID());
        //从ACKList里面去除此消息
        unprocessedRequests.finish(header.getMID());
    }
}
