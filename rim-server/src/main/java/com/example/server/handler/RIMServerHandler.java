package com.example.server.handler;

import com.example.common.constant.Constants;
import com.example.common.model.RIMUserInfo;
import com.example.common.proto.Header;
import com.example.common.proto.IM_Message;
import com.example.common.util.NettyAttrUtil;
import com.example.server.inactive.InactiveHandler;
import com.example.server.service.SendOfflineMessage;
import com.example.server.service.UserService;
import com.example.server.util.SpringBeanFactory;
import com.example.server.util.UserChannelFactory;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class RIMServerHandler extends SimpleChannelInboundHandler<IM_Message> {

    InactiveHandler inactiveHandler;

    SendOfflineMessage sendOfflineMessage;

    UserService userService;

    /**
     * 定期剔除掉线的客户端
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                log.info("检查客户端是否存活..");
                if (inactiveHandler == null)
                    inactiveHandler = SpringBeanFactory.getBean("InactiveHandler", InactiveHandler.class);
                inactiveHandler.handler(ctx);
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    public void channelActive(ChannelHandlerContext ctx) {
        log.info("客户端连接成功，[{}]通道激活！", ctx.channel().localAddress().toString());
    }

    /**
     * 取消绑定
     *
     * @param ctx
     */
    public void channelInactive(ChannelHandlerContext ctx) {
        //可能出现业务判断离线后再次触发channelInactive
        RIMUserInfo userId = UserChannelFactory.getUserId(ctx.channel());
        if (userId != null) {
            if (userService == null) {
                userService = SpringBeanFactory.getBean(UserService.class);
            }
            //用户离线
            userService.userOffline(userId.getUID());
            //删除绑定
            UserChannelFactory.remove(ctx.channel());
            ctx.channel().close();
            log.info("用户[{}]已离线", userId.getUID());
        }
    }

    /**
     * 接收数据并处理
     *
     * @param ch
     * @param message
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ch, IM_Message message) {
        log.info("收到消息：{}", message);
        Header header = message.getHeader();
        if (header.getType() == Constants.CommandType.LOGIN) {
            UserChannelFactory.put(header.getUID(), ch.channel());
            log.info("用户[{}]注册成功", header.getUID());
            NettyAttrUtil.updateReadTime(ch.channel(), System.currentTimeMillis());
            log.info("更新心跳时间成功！");
            if (sendOfflineMessage == null) {
                sendOfflineMessage = SpringBeanFactory.getBean("SendOfflineMessage", SendOfflineMessage.class);
            }
            log.info("离线消息发送中...");
            sendOfflineMessage.send(message);
        } else if (header.getType() == Constants.CommandType.PING) {
            log.info("收到客户端{}心跳！", UserChannelFactory.getUserId(ch.channel()).getUID());
            NettyAttrUtil.updateReadTime(ch.channel(), System.currentTimeMillis());
            log.info("更新心跳时间成功！");
            IM_Message heartBeat = new IM_Message();
            Header head = new Header();
            head.setType(Constants.CommandType.PONG);
            heartBeat.setHeader(head);
            ch.writeAndFlush(heartBeat).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("心跳pong发送成功！");
                } else
                    log.info("心跳pong发送失败！");
            });
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.info("连接出现异常：{}", cause.getCause().toString());
    }
}
