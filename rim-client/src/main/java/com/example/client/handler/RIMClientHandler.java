package com.example.client.handler;

import com.example.client.handler.strategy.MessageReceivedContext;
import com.example.client.util.SpringBeanFactory;
import com.example.common.proto.IM_Message;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@ChannelHandler.Sharable
@Slf4j(topic = "RIMClientHandler")
public class RIMClientHandler extends SimpleChannelInboundHandler{

    MessageReceivedContext context;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg){

        IM_Message message = (IM_Message) msg;
        if(context == null){
            context = SpringBeanFactory.getBean(MessageReceivedContext.class);
        }
        context.handle(message);
    }
}
