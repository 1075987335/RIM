package com.example.server.server;

import com.example.server.config.ServerConfig;
import com.example.server.config.ShutDownHook;
import com.example.server.init.RIMSereverInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j(topic = "RIMServer")
public class RIMServer {
    @Autowired
    ServerConfig config;
    @Autowired
    ShutDownHook shutDownHook;
    private final EventLoopGroup boss = new NioEventLoopGroup();
    private final EventLoopGroup work = new NioEventLoopGroup();

    @PostConstruct
    public void start() throws InterruptedException {
        shutDownHook.clearAll();
        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(boss, work)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new RIMSereverInitializer());
        ChannelFuture future = bootstrap.bind(config.getServerPort()).sync();
        if (future.isSuccess()) {
            log.info("服务器启动成功！");
        } else {
            log.error("服务器启动失败！");
        }
    }
}
