package com.example.route.service;

import com.example.client.request.SendMessageVo;
import com.example.common.constant.Constants;
import com.example.common.proto.IM_Message;
import com.example.common.util.RingBufferWheel;
import com.example.route.thread.ack.GroupAckJob;
import com.example.route.thread.ack.P2PAckJob;
import com.example.route.thread.ack.UnprocessedRequests;
import com.example.route.utils.ConvertToIM_Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BeforeSendService {

    @Autowired
    UnprocessedRequests unprocessedRequests;

    @Autowired
    RingBufferWheel ringBufferWheel;

    @Value("${ringBufferWheel.delay-time}")
    int delay_time;

    /**
     * 将消息添加进ACK等待列表中
     * @param sendMessageVo
     */
    public void addToAckList(SendMessageVo sendMessageVo, long TID){
        IM_Message message = ConvertToIM_Message.convert(sendMessageVo, TID);
        unprocessedRequests.put(message);
        byte type = sendMessageVo.getType();
        if(type == Constants.CommandType.P2P_MSG){
            P2PAckJob p2PAckJob =new P2PAckJob(message.getHeader().getMID(),3);
            p2PAckJob.setDelay_time(delay_time);
            p2PAckJob.setTaskId(sendMessageVo.getMID());
            ringBufferWheel.addTask(p2PAckJob);
        }
        else if(type == Constants.CommandType.GROUP_MSG){
            long mid = message.getHeader().getMID();
            long tid = message.getHeader().getTID();
            long gid = message.getHeader().getGID();
            GroupAckJob groupAckJob = new GroupAckJob(mid, tid, gid, 3);
            groupAckJob.setDelay_time(delay_time);
            groupAckJob.setTaskId(sendMessageVo.getMID() & TID);
            ringBufferWheel.addTask(groupAckJob);
        }
    }
}
