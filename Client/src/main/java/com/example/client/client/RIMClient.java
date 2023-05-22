package com.example.client.client;

import com.example.client.config.UserInfo;
import com.example.client.init.RIMClientHandlerInitializer;
import com.example.client.request.LoginVO;
import com.example.client.response.ServerInfo;
import com.example.client.service.UserService;
import com.example.client.thread.duplicate.RemoveReceivedJob;
import com.example.client.thread.reconnect.ReConnectManager;
import com.example.client.util.SingleChannelFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j(topic = "RIMClient")
public class RIMClient {
    private EventLoopGroup group;

    private ServerInfo serverInfo;

    @Autowired
    private UserService userService;

    @Autowired
    private SingleChannelFactory channelFactory;

    @Autowired
    private UserInfo userInfo;

    @Autowired
    private ReConnectManager reConnectManager;

    @Autowired
    private ScheduledThreadPoolExecutor scheduleJobExecutor;

    Runnable task = null;

    public void start(){
        LoginVO loginVO = new LoginVO();
        loginVO.setUID(userInfo.getUserID());
        //登陆操作
        serverInfo = userService.login(loginVO);
        //根据登陆返回的服务器信息进行client服务器启动
        startClient();
        //向server注册，发送登陆包
        userService.loginServer();

        //开启消息检测
        task = new Thread(new RemoveReceivedJob());
        scheduleJobExecutor.scheduleAtFixedRate(task, 120, 120, TimeUnit.SECONDS);
    }

    /**
     * 开启netty服务
     */
    public void startClient(){
        group = new NioEventLoopGroup(0,new DefaultThreadFactory("work"));
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new RIMClientHandlerInitializer());
        ChannelFuture future = null;
        try{
            future = bootstrap.connect(new InetSocketAddress(serverInfo.getIp(),serverInfo.getServerPort())).sync();
        }
        catch (Exception e){
            log.error("服务器连接失败！");
        }

        if (future.channel() != null) {
            channelFactory.setChannel(future.channel());
        }
        else{
            log.error("服务器连接失败，channel为null");
        }
    }

    /**
     * 重连
     */
    public boolean reConnect(){
        Channel channel = channelFactory.getChannel();
        if(channel != null && channel.isActive()){
            log.info("当前通道活跃，无需重连！");
            return true;
        }
        //用户下线
        userService.offline();
        //关闭服务器
        close();
        //重新开启登陆操作
        try {
            start();
        } catch (Exception e) {
            return false;
        }

        //成功，关闭重试任务
        reConnectManager.closePool();
        return true;
    }

    /**
     * 关闭通道
     */
    public void close(){
        Channel channel = channelFactory.getChannel();
        if (channel != null) {
            channel.close();
        }

        //清除工厂信息
        channelFactory.clear();

        //移除定时任务
        scheduleJobExecutor.remove(task);
        log.info("服务器关闭成功！");
    }
}
