package com.example.server.init;

import com.example.common.codec.MessageDecode;
import com.example.common.codec.MessageEncode;
import com.example.server.handler.RIMServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

public class RIMSereverInitializer extends ChannelInitializer<SocketChannel> {
    private final RIMServerHandler rimServerHandle = new RIMServerHandler() ;
    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        channel.pipeline()
                .addLast(new MessageDecode())
                .addLast(new MessageEncode())
                .addLast(new IdleStateHandler(160,0,0))
                .addLast(rimServerHandle);
    }
}
