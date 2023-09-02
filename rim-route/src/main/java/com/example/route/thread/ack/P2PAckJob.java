package com.example.route.thread.ack;


import com.example.MessageSendService;
import com.example.client.request.SendMessageVo;
import com.example.common.proto.IM_Message;
import com.example.common.util.RingBufferWheel;
import com.example.route.utils.ConvertToSendMessageVo;
import com.example.route.utils.SpringBeanFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "AckJob")
public class P2PAckJob extends RingBufferWheel.Task {

    long messageId;

    int count;

    int index;


    UnprocessedRequests unprocessedRequests;

    RingBufferWheel ringBufferWheel;

    MessageSendService messageSendService;


    public P2PAckJob(long messageId, int count) {
        this.messageId = messageId;
        this.count = count;
        index = 1;
        unprocessedRequests = SpringBeanFactory.getBean(UnprocessedRequests.class);
        ringBufferWheel = SpringBeanFactory.getBean(RingBufferWheel.class);
        messageSendService = SpringBeanFactory.getBean(MessageSendService.class);
    }

    @Override
    public void run() {
        if (index < count) {
            index++;
            IM_Message message = unprocessedRequests.get(messageId);
            if (message == null) {
                log.info("消息处理成功！重传结束！");
                return;
            }
            log.info("消息确认超时，开始重传！");
            ringBufferWheel.addTask(this);
            SendMessageVo sendMessageVo = ConvertToSendMessageVo.parse(message);
            messageSendService.sendRetryMessageToServer(sendMessageVo);
        } else {
            //如果重发失败，将消息从ACKLIST里面去除
            log.error("重发失败，请重新连接服务器！离线消息存储中...");
            IM_Message message = unprocessedRequests.get(messageId);
            messageSendService.storeOfflineMessage(ConvertToSendMessageVo.parse(message));
        }
    }
}
