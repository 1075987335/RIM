package com.example.thread;

import com.example.common.constant.Constants;
import com.example.common.proto.IM_Message;
import com.example.service.StoreMessageService;
import com.example.util.SpringBeanFactory;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StoreJob implements Runnable{

    Channel channel;

    StoreMessageService service;

    long deliveryTag;

    IM_Message message;

    boolean isOffline;

    public StoreJob(IM_Message message, Channel channel, long deliveryTag, boolean isOffline){
        service = SpringBeanFactory.getBean(StoreMessageService.class);
        this.channel = channel;
        this.deliveryTag = deliveryTag;
        this.message = message;
        this.isOffline = isOffline;
    }
    @SneakyThrows
    @Override
    public void run() {
        byte type = message.getHeader().getType();
        try {
            if(isOffline == true && type == Constants.CommandType.P2P_MSG){
                service.doStoreP2POfflineMessage(message);
            }
            if(type == Constants.CommandType.P2P_MSG){
                service.doStoreP2PMessage(message);
            }
            else if(type == Constants.CommandType.P2P_ACK){
                service.doStoreP2PAck(message);
            }
            else if(type == Constants.CommandType.GROUP_MSG){
                service.doStoreGroupMessage(message);
            }
            else if(type == Constants.CommandType.GROUP_ACK){
                service.doStoreGroupACK(message);
            }
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            channel.basicNack(deliveryTag, true, false);
        }
    }
}
