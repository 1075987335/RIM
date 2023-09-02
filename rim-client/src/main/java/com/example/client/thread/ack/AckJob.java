package com.example.client.thread.ack;

import com.example.client.request.SendMessageVo;
import com.example.client.service.send.SendMessage;
import com.example.client.util.SpringBeanFactory;
import com.example.common.util.RingBufferWheel;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "AckJob")
public class AckJob extends RingBufferWheel.Task {

    long messageId;

    int count;

    int index;

    UnprocessedRequests unprocessedRequests;

    SendMessage sendMessage;

    RingBufferWheel ringBufferWheel;

    public AckJob(long messageId, int count) {
        this.messageId = messageId;
        this.count = count;
        index = 1;
        unprocessedRequests = SpringBeanFactory.getBean(UnprocessedRequests.class);
        sendMessage = SpringBeanFactory.getBean(SendMessage.class);
        ringBufferWheel = SpringBeanFactory.getBean(RingBufferWheel.class);
    }

    @Override
    public void run() {
        if (index <= count) {
            SendMessageVo message = unprocessedRequests.get(messageId);
            if (message == null) {
                if (index == 1) {
                    log.info("消息已处理成功，无需重传");
                } else {
                    log.info("消息已处理成功！重传结束！");
                }
                return;
            }
            log.info("ACK超时，开始第{}次重传", index);
            ringBufferWheel.addTask(this);
            sendMessage.send(message);
            index++;
        } else {
            log.error("重发失败，请重新连接服务器！");
        }
    }
}
