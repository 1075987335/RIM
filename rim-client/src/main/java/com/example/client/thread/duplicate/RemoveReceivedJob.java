package com.example.client.thread.duplicate;

import com.example.client.util.SpringBeanFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RemoveReceivedJob implements Runnable{

    ReceivedMessage receivedMessage;

    public RemoveReceivedJob(){
        receivedMessage = SpringBeanFactory.getBean(ReceivedMessage.class);
    }


    @Override
    public void run() {
        log.info("开始删除过期的已接收消息...");
        receivedMessage.checkDuplicate();
    }
}
