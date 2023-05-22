package com.example.thread;

import com.example.common.proto.IM_Message;
import com.example.service.StoreMessageService;
import com.example.util.SpringBeanFactory;

public class GroupAckJob implements Runnable{
    StoreMessageService service;

    IM_Message message;

    public GroupAckJob(IM_Message message){
        service = SpringBeanFactory.getBean(StoreMessageService.class);
        this.message = message;
    }
    @Override
    public void run() {
        service.doStoreGroupACK(message);
    }
}
