package com.example.client.init;

import com.example.client.handler.HeartBeatHandler;
import com.example.client.handler.RIMClientHandler;
import com.example.common.codec.MessageDecode;
import com.example.common.codec.MessageEncode;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.IdleStateHandler;


public class RIMClientHandlerInitializer extends ChannelInitializer<Channel> {
    private final RIMClientHandler rimClientHandler=new RIMClientHandler();

    private final HeartBeatHandler heartbeatHandler = new HeartBeatHandler();
    @Override
    protected void initChannel(Channel channel) throws Exception {
        channel.pipeline()
                .addLast(new MessageEncode())
                .addLast(new MessageDecode())
                .addLast(new IdleStateHandler(0,150,0))
                .addLast(rimClientHandler)
                .addLast(heartbeatHandler);
    }
}
