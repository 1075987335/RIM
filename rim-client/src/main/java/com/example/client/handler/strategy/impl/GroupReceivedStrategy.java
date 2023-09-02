package com.example.client.handler.strategy.impl;

import com.example.client.config.UserInfo;
import com.example.client.handler.strategy.MessageReceivedStrategy;
import com.example.client.response.AckVo;
import com.example.client.service.send.SendMessage;
import com.example.client.thread.duplicate.ReceivedMessage;
import com.example.client.util.ConvertToAckVo;
import com.example.common.constant.Constants;
import com.example.common.proto.Header;
import com.example.common.proto.IM_Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GroupReceivedStrategy implements MessageReceivedStrategy {

    @Autowired
    SendMessage sendMessage;

    @Autowired
    ReceivedMessage receivedMessage;

    @Autowired
    UserInfo userInfo;

    @Override
    public void handle(IM_Message message) {
        Header header = message.getHeader();

        //保证消息不会发给自己
        if (header.getUID() != userInfo.getUserID()) {
            log.info("\n" +
                            "--------------------------------------------\n" +
                            "来自群组 [{}] 用户 [{}] : {}\n" +
                            "--------------------------------------------",
                    header.getGID(), header.getUID(), message.getBody());
        }

        //进行幂等性判断
        if (!receivedMessage.isGroupMessageExist(header.getMID())) {
            receivedMessage.putGroupReceivedMessage(header.getMID(), System.currentTimeMillis());
            AckVo ackVo = ConvertToAckVo.convert(message, Constants.CommandType.GROUP_ACK);
            //发送接收ack
            sendMessage.sendACK(ackVo);
        } else {
            log.info("幂等性处理...");
        }
    }
}
