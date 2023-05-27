package com.example.route.service.strategy.offlineSend.impl;

import com.example.RouteUserService;
import com.example.client.request.SendMessageVo;
import com.example.common.proto.IM_Message;
import com.example.route.service.BeforeSendService;
import com.example.route.service.strategy.offlineSend.OfflineMessageSendStrategy;
import com.example.route.thread.SendMessageJob;
import com.example.route.utils.ConvertToIM_Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadPoolExecutor;

@Component
@Slf4j
public class P2POfflineSendStrategy implements OfflineMessageSendStrategy {

    @Autowired
    BeforeSendService beforeSendService;

    @Autowired
    RouteUserService userService;

    @Autowired
    ThreadPoolExecutor sendJobExecutor;


    @Override
    public void process(SendMessageVo sendMessageVo) {

        IM_Message message = ConvertToIM_Message.convert(sendMessageVo, sendMessageVo.getTID());

        //单聊查看用户登陆状态
        if (userService.getUserLoginState(sendMessageVo.getTID())) {
            log.info("用户{}已离线，无法发送离线消息...",sendMessageVo.getTID());
            return;
        }

        //加入到ACK等待列表中
        beforeSendService.addToAckList(sendMessageVo, sendMessageVo.getTID());

        //发送消息
        sendJobExecutor.execute(new SendMessageJob(sendMessageVo, sendMessageVo.getTID()));
    }
}
