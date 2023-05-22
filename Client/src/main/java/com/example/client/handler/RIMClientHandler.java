package com.example.client.handler;

import com.example.client.handler.strategy.MessageReceivedContext;
import com.example.client.service.send.SendMessage;
import com.example.client.thread.ack.UnprocessedRequests;
import com.example.client.thread.duplicate.ReceivedMessage;
import com.example.client.util.SpringBeanFactory;
import com.example.common.proto.IM_Message;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@ChannelHandler.Sharable
@Slf4j(topic = "RIMClientHandler")
public class RIMClientHandler extends SimpleChannelInboundHandler{

    UnprocessedRequests unprocessedRequests;

    ReceivedMessage receivedMessage;

    SendMessage sendMessage;

    MessageReceivedContext context;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg){

        IM_Message message = (IM_Message) msg;
        log.info("收到消息： {}",message);
        if(context == null){
            context = SpringBeanFactory.getBean(MessageReceivedContext.class);
        }
        context.handle(message);

//        if(type == Constants.CommandType.PONG){
//            log.info("收到服务端心跳！");
//        }
//        else if(type == Constants.CommandType.P2P_ACK || type == Constants.CommandType.GROUP_ACK){
//            log.info("收到消息[{}]的{}类型的ACK", header.getMID(), type);
//            if(unprocessedRequests == null){
//                unprocessedRequests = SpringBeanFactory.getBean(UnprocessedRequests.class);
//            }
//            unprocessedRequests.finish(header.getMID());
//        }
//        else if(type == Constants.CommandType.P2P_MSG){
//            log.info("From {} says: {}", header.getUID(), IMMessage.getBody());
//            if(sendMessage == null){
//                sendMessage = SpringBeanFactory.getBean(SendMessage.class);
//            }
//            if(receivedMessage == null){
//                receivedMessage = SpringBeanFactory.getBean(ReceivedMessage.class);
//            }
//            //进行幂等性判断
//            if (!receivedMessage.isP2PMessageExist(header.getMID())) {
//                receivedMessage.putP2PReceivedMessage(header.getMID(), System.currentTimeMillis());
//                AckVo ackVo = new AckVo();
//                ackVo.setTID(header.getTID());
//                ackVo.setMID(header.getMID());
//                ackVo.setUID(header.getUID());
//                ackVo.setType(Constants.CommandType.P2P_ACK);
//                //发送接收ack
//                sendMessage.sendACK(ackVo);
//            }
//        }
//        else if(type == Constants.CommandType.GROUP_MSG){
//            log.info("From group {} user {} say: {}", header.getGID(), header.getUID(), ((IM_Message) msg).getBody());
//            if(sendMessage == null){
//                sendMessage = SpringBeanFactory.getBean(SendMessage.class);
//            }
//            if(receivedMessage == null){
//                receivedMessage = SpringBeanFactory.getBean(ReceivedMessage.class);
//            }
//            //进行幂等性判断
//            if (!receivedMessage.isGroupMessageExist(header.getMID())) {
//                receivedMessage.putGroupReceivedMessage(header.getMID(), System.currentTimeMillis());
//                AckVo ackVo = new AckVo();
//                ackVo.setMID(header.getMID());
//                ackVo.setUID(header.getTID());
//                ackVo.setGID(header.getGID());
//                ackVo.setType(Constants.CommandType.GROUP_ACK);
//                //发送接收ack
//                sendMessage.sendACK(ackVo);
//            }
//        }
    }
}
