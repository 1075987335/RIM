package com.example.client.service.command;

import com.example.client.config.UserInfo;
import com.example.client.request.SendMessageVo;
import com.example.client.service.InnerCommand;
import com.example.client.service.id.GetIdService;
import com.example.client.service.send.SendMessage;
import com.example.client.thread.ack.AckJob;
import com.example.client.thread.ack.UnprocessedRequests;
import com.example.client.util.ConvertToSendMessageVo;
import com.example.common.constant.Constants;
import com.example.common.util.RingBufferWheel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class GroupMsgCommand implements InnerCommand {
    @Autowired
    UserInfo userInfo;

    @Autowired
    SendMessage sendMessage;

    @Autowired
    UnprocessedRequests unprocessedRequests;

    @Autowired
    RingBufferWheel ringBufferWheel;

    @Autowired
    GetIdService getIdService;

    @Value("${ringbufferwheel.count}")
    int count;

    @Value("${ringbufferwheel.delay-time}")
    int delay_time;

    @Override
    public void process(String msg) {
        if (msg.split(" ").length <= 2) {
            log.info("incorrect command, :group [groupID] [msg]");
            return;
        }
        String[] totalMsg = msg.split(" ");
        long id = getIdService.getNextId("key");
        SendMessageVo parse = ConvertToSendMessageVo.convert(totalMsg, userInfo, id, Constants.CommandType.GROUP_MSG);
        //加入acklist
        unprocessedRequests.put(parse.getMID(), parse);
        AckJob ackJob = new AckJob(parse.getMID(), count);
        ackJob.setDelay_time(delay_time);
        ackJob.setTaskId(parse.getMID());
        ringBufferWheel.addTask(ackJob);
        sendMessage.send(parse);
    }
}
