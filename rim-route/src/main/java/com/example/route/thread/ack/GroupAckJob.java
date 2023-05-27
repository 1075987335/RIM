package com.example.route.thread.ack;

import com.example.MessageSendService;
import com.example.client.request.SendMessageVo;
import com.example.common.proto.IM_Message;
import com.example.common.util.RingBufferWheel;
import com.example.route.utils.ConvertToSendMessageVo;
import com.example.route.utils.SpringBeanFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GroupAckJob extends RingBufferWheel.Task {

    long mid;

    long tid;

    long gid;

    int count;

    int index;

    UnprocessedRequests unprocessedRequests;

    RingBufferWheel ringBufferWheel;

    MessageSendService messageSendService;

    public GroupAckJob(long mid, long tid, long gid, int count){
        this.mid = mid;
        this.gid = gid;
        this.tid = tid;
        this.count = count;
        index = 1;
        unprocessedRequests = SpringBeanFactory.getBean(UnprocessedRequests.class);
        ringBufferWheel = SpringBeanFactory.getBean(RingBufferWheel.class);
        messageSendService = SpringBeanFactory.getBean(MessageSendService.class);
    }

    @Override
    public void run(){
        if (index < count) {
            index++;
            IM_Message message = unprocessedRequests.get(mid, tid, gid);
            if(message == null){
                log.info("消息处理成功！重传结束！");
                return;
            }
            log.info("群聊消息确认超时，开始重传！");
            ringBufferWheel.addTask(this);
            SendMessageVo sendMessageVo = ConvertToSendMessageVo.parse(message);
            messageSendService.sendRetryMessageToServer(sendMessageVo);
        }
        else{
            //如果重发失败，不做处理
            unprocessedRequests.finish(mid, tid, gid);
            log.error("重发失败，请重新连接服务器！");
        }
    }

}
