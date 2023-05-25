package com.example.mq;

import com.example.common.serializer.kyro.KryoSerializer;
import com.example.common.proto.IM_Message;
import com.example.service.StoreMessageService;
import com.example.thread.StoreJob;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ThreadPoolExecutor;

@Component
@RabbitListener(queues = "storeP2PACK")
@Slf4j(topic = "P2PACKReceiver")
public class P2PACKReceiver {

    @Autowired
    StoreMessageService service;

    @Autowired
    KryoSerializer kryoSerializer;

    @Autowired
    ThreadPoolExecutor storeJobExecutor;

    @RabbitHandler
    public void process(byte[] msg, Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        IM_Message imMessage =kryoSerializer.deserialize(msg, IM_Message.class);
        log.info("[P2PAckMQ]收到消息：{}", imMessage);
        try {
            storeJobExecutor.execute(new StoreJob(imMessage, channel, deliveryTag, false));
        } catch (Exception e) {
            channel.basicNack(deliveryTag, true, false);
            throw new RuntimeException(e);
        }
    }
}
