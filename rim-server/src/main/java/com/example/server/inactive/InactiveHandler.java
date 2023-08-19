package com.example.server.inactive;

import com.example.RouteUserService;
import com.example.client.request.OfflineReqVO;
import com.example.common.model.RIMUserInfo;
import com.example.common.util.NettyAttrUtil;
import com.example.server.config.ServerConfig;
import com.example.server.util.UserChannelFactory;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component(value = "InactiveHandler")
public class InactiveHandler {

    @DubboReference
    RouteUserService routeUserService;

    @Autowired
    ServerConfig config;

    public void offline(RIMUserInfo userInfo){
        OfflineReqVO offlineReqVO =new OfflineReqVO();
        offlineReqVO.setUID(userInfo.getUID());
        routeUserService.offLine(offlineReqVO);
        log.info("用户{}离线",userInfo.getUID());
    }

    public void handler(ChannelHandlerContext ctx){
        long heartBeatTime = config.getHeartBeatTime();
        Long lastReadTime = NettyAttrUtil.getReadTime(ctx.channel());
        long nowTime = System.currentTimeMillis();
        if(lastReadTime != null && nowTime - lastReadTime > heartBeatTime){
            RIMUserInfo userId = UserChannelFactory.getUserId(ctx.channel());
            if(userId != null){
                log.warn("客户端[{}]心跳超时[{}]ms，需要关闭连接！",userId.getUID(),nowTime - lastReadTime);
            }
            if(userId != null){
                offline(userId);
                UserChannelFactory.remove(ctx.channel());
                ctx.channel().close();
                log.info("连接已关闭！");
            }
            else
                log.info("userId为null，无法判断存活状态");
        }
        else{
            log.info("客户端未超时");
        }
    }
}
