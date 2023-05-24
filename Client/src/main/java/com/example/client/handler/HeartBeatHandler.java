package com.example.client.handler;

import com.example.client.config.ClientState;
import com.example.client.service.send.SendMessage;
import com.example.client.thread.reconnect.ReConnectManager;
import com.example.client.util.SpringBeanFactory;
import com.example.common.constant.Constants;
import com.example.common.proto.Header;
import com.example.common.proto.IM_Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "HeartBeatHandler")
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {

    private SendMessage sendMessage;

    private ClientState clientState;

    private ReConnectManager reConnectManager;

    /**
     * 处理断线重连逻辑
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx){
        if(clientState == null){
            clientState = SpringBeanFactory.getBean(ClientState.class);
        }
        if(clientState.isNormalClose() == true)
            return;
        log.info("服务器断开，尝试重连！");
        if(reConnectManager == null)
            reConnectManager = SpringBeanFactory.getBean(ReConnectManager.class);
        reConnectManager.reConnect(3);
        ctx.fireChannelActive();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt){
        if(evt instanceof IdleStateEvent){
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if(idleStateEvent.state() == IdleState.WRITER_IDLE){
                IM_Message heartBeat = new IM_Message();
                Header header = new Header();
                header.setType(Constants.CommandType.PING);
                heartBeat.setHeader(header);
                sendMessage = SpringBeanFactory.getBean("SendMessage",SendMessage.class);
                sendMessage.send(heartBeat);
            }
        }
    }
}
