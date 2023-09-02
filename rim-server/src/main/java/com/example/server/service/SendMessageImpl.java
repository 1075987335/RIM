package com.example.server.service;

import com.example.common.proto.Header;
import com.example.common.proto.IM_Message;
import com.example.server.SendMessage;
import com.example.server.reqeust.SendRequestVo;
import com.example.server.util.UserChannelFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

@DubboService
@Service
@Slf4j
public class SendMessageImpl implements SendMessage {
    @Override
    public String SendMessage(SendRequestVo sendRequestVo) {
        IM_Message msg = new IM_Message();
        Header header = new Header();
        header.setTID(sendRequestVo.getTID());
        header.setUID(sendRequestVo.getUID());
        header.setMID(sendRequestVo.getMID());
        header.setType(sendRequestVo.getType());
        header.setGID(sendRequestVo.getGID());
        msg.setBody(sendRequestVo.getBody());
        msg.setHeader(header);
        log.info("即将推送消息：{}", msg);
        Channel channel = UserChannelFactory.get(sendRequestVo.getTID());
        ChannelFuture future = channel.writeAndFlush(msg);
        future.addListener((ChannelFutureListener) channelFuture -> {
            log.info("消息已推送！");
        });
        return "success";
    }
}
