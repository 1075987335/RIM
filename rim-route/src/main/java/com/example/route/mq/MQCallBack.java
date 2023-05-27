package com.example.route.mq;


import com.example.MessageSendService;
import com.example.client.response.AckVo;
import com.example.common.constant.Constants;
import com.example.common.util.RingBufferWheel;
import com.example.route.thread.ack.UnprocessedRequests;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j(topic = "MQCallBack")
public class MQCallBack implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnsCallback {

    @Autowired
    MessageSendService messageSendService;

    @Autowired
    UnprocessedRequests unprocessedRequests;

    @Autowired
    RingBufferWheel outTimeJobExecutor;

    @Override
    public void confirm(CorrelationData correlationData, boolean b, String s) {
        if(b){
            log.info("收到回调消息，id为:{}", correlationData.toString());
            String id = correlationData.getId();
            String[] messageInfo = id.split(":");
            String messageType = messageInfo[0];
            if(messageType.equals("P2P")){
                long mid = Long.parseLong(messageInfo[1]);
                long uid = Long.parseLong(messageInfo[2]);
                AckVo ackVo = new AckVo();
                ackVo.setType(Constants.CommandType.P2P_ACK);
                ackVo.setTID(uid);
                ackVo.setMID(mid);
                messageSendService.sendAckToClient(ackVo);
            }
            else if(messageType.equals("Group")){
                long mid = Long.parseLong(messageInfo[1]);
                long uid = Long.parseLong(messageInfo[2]);
                AckVo ackVo = new AckVo();
                ackVo.setType(Constants.CommandType.GROUP_ACK);
                ackVo.setTID(uid);
                ackVo.setMID(mid);
                messageSendService.sendAckToClient(ackVo);
            }
            else if(messageType.equals("P2PAck")){
                long mid = Long.parseLong(messageInfo[1]);
                outTimeJobExecutor.cancel(mid);
                unprocessedRequests.finish(mid);
            }
            else if(messageType.equals("GroupAck")){
                long mid = Long.parseLong(messageInfo[1]);
                long tid = Long.parseLong(messageInfo[2]);
                long gid = Long.parseLong(messageInfo[3]);
                outTimeJobExecutor.cancel(mid & tid);
                unprocessedRequests.finish(mid, tid, gid);
            }
            else if(messageType.equals("Off")){
                long mid = Long.parseLong(messageInfo[1]);
                outTimeJobExecutor.cancel(mid);
                unprocessedRequests.finish(mid);
            }
        }
        else{
            log.error("MQ消息推送失败！");
        }
    }

    @Override
    public void returnedMessage(ReturnedMessage returnedMessage) {

    }
}
