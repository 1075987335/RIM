package com.example.route.service.strategy.send;

import com.example.client.request.SendMessageVo;
import com.example.common.enums.MessageSendStrategyEnum;
import com.example.route.utils.SpringBeanFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class MessageSendContext {
    Map<Byte, String> allClazz;

    public void handle(SendMessageVo sendMessageVo){
        if(allClazz == null){
            allClazz = MessageSendStrategyEnum.getAllClazz();
        }
        try {
            MessageSendStrategy strategy = (MessageSendStrategy)SpringBeanFactory.getBean(Class.forName(allClazz.get(sendMessageVo.getType())));
            strategy.process(sendMessageVo);
        } catch (ClassNotFoundException e) {
            log.info("策略获取失败...");
        }
    }
}
