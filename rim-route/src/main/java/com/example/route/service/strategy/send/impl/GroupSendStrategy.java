package com.example.route.service.strategy.send.impl;

import com.example.RouteUserService;
import com.example.client.request.SendMessageVo;
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

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

@Component
@Slf4j
public class GroupSendStrategy implements MessageSendStrategy {

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
        List<Long> allGroupMember = redisUtil.getAllGroupMember(sendMessageVo.getGID());
        IM_Message message = ConvertToIM_Message.convert(sendMessageVo, sendMessageVo.getTID());
        mqSend.sendMessage(message, false);
        for (Long id : allGroupMember) {
            if (redisUtil.getUserLoginState(id)) {
                beforeSendService.addToAckList(sendMessageVo, id);
                sendJobExecutor.execute(new SendMessageJob(sendMessageVo, id));
            }
        }
    }
}
