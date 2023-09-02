package com.example.client.handler.strategy;

import com.example.client.util.SpringBeanFactory;
import com.example.common.enums.MessageReceivedStrategyEnum;
import com.example.common.proto.Header;
import com.example.common.proto.IM_Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class MessageReceivedContext {

    Map<Byte, String> allClazz;

    public void handle(IM_Message message) {
        Header header = message.getHeader();
        if (allClazz == null) {
            allClazz = MessageReceivedStrategyEnum.getAllClazz();
        }
        try {
            MessageReceivedStrategy strategy = (MessageReceivedStrategy) SpringBeanFactory.getBean(Class.forName(allClazz.get(header.getType())));
            strategy.handle(message);
        } catch (ClassNotFoundException e) {
            log.info("获取策略失败...");
        }
    }
}
