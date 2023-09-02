package com.example.client.service.send;

import com.example.MessageSendService;
import com.example.client.request.SendMessageVo;
import com.example.client.response.AckVo;
import com.example.client.thread.ack.UnprocessedRequests;
import com.example.client.util.SingleChannelFactory;
import com.example.common.constant.Constants;
import com.example.common.proto.IM_Message;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("SendMessage")
@Slf4j(topic = "SendMessage")
public class SendMessage {
    @Autowired
    private SingleChannelFactory channelFactory;

    @DubboReference
    MessageSendService messageSendService;

    @Autowired
    UnprocessedRequests unprocessedRequests;


    /**
     * 发送消息，包括单聊和群聊消息
     *
     * @param sendMessageVo
     */
    public void send(SendMessageVo sendMessageVo) {
        log.info("即将发送消息：{}", sendMessageVo);
        messageSendService.sendMessageToServer(sendMessageVo);
    }

    /**
     * 向路由层以及存储服务发送ack
     *
     * @param ackVo
     */
    public void sendACK(AckVo ackVo) {
        log.info("即将发送ack消息：{}", ackVo);
        messageSendService.sendAckToStoreService(ackVo);
    }


    /**
     * 直接向服务器发送消息，包括login登陆包和心跳包
     *
     * @param msg
     */
    public void send(IM_Message msg) {
        Channel channel = channelFactory.getChannel();
        if (channel.isActive()) {
            ChannelFuture future = channel.writeAndFlush(msg);
            future.addListener((ChannelFutureListener) channelFuture -> {
                byte type = msg.getHeader().getType();
                if (type == Constants.CommandType.PING && channelFuture.isSuccess()) {
                    log.info("心跳ping发送成功！");
                } else if (type == Constants.CommandType.PING && !channelFuture.isSuccess()) {
                    log.error("心跳ping发送失败！");
                } else if (type == Constants.CommandType.LOGIN && channelFuture.isSuccess()) {
                    log.info("登陆消息发送成功！");
                } else if (type == Constants.CommandType.LOGIN && channelFuture.isSuccess()) {
                    log.error("登陆消息发送失败！");
                }
            });
        } else {
            log.error("通道失活，消息发送失败！");
        }
    }
}
