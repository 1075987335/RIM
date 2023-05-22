package com.example.route.service.strategy.send.impl;

import com.example.RouteUserService;
import com.example.client.request.SendMessageVo;
import com.example.common.constant.Constants;
import com.example.common.proto.IM_Message;
import com.example.common.util.RedisUtil;
import com.example.route.loadbalance.consistenthash.ConsistentHashHandle;
import com.example.route.mq.MQSend;
import com.example.route.service.BeforeSendService;
import com.example.route.service.strategy.send.MessageSendStrategy;
import com.example.route.thread.SendMessageJob;
import com.example.route.utils.ConvertToIM_Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadPoolExecutor;

@Component
@Slf4j
public class P2PSendStrategy implements MessageSendStrategy {
    @Autowired
    ConsistentHashHandle handle;

    @Autowired
    MQSend mqSend;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    BeforeSendService beforeSendService;

    @Autowired
    RouteUserService userService;

    @Autowired
    ThreadPoolExecutor sendJobExecutor;

    @Override
    public void process(SendMessageVo sendMessageVo) {

        //加入到ACK等待列表中，用于存储转发的消息或者要保存的离线消息
        beforeSendService.addToAckList(sendMessageVo, sendMessageVo.getTID());

        IM_Message message = ConvertToIM_Message.convert(sendMessageVo, sendMessageVo.getTID());

        //单聊查看用户登陆状态
        if (sendMessageVo.getType() == Constants.CommandType.P2P_MSG && !userService.getUserLoginState(sendMessageVo.getTID())) {
            log.info("用户{}已离线！对消息进行离线存储...",sendMessageVo.getTID());
            //保存离线消息到redis中
            mqSend.sendMessage(message, true);
            //这个是为了给客户端发送ack消息，并且保存在mysql中进行持久化存储
            mqSend.sendMessage(message, false);
            return;
        }

        mqSend.sendMessage(message, false);
        //发送消息
        sendJobExecutor.execute(new SendMessageJob(sendMessageVo, sendMessageVo.getTID()));
    }
}
