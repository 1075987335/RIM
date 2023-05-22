package com.example.server.service;

import com.example.OfflineMessageService;
import com.example.common.proto.IM_Message;
import com.example.storeapi.vo.OfflineRequestVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

@Component(value = "SendOfflineMessage")
public class SendOfflineMessage {

    @DubboReference
    OfflineMessageService offlineMessageService;

    public void send(IM_Message message){
        OfflineRequestVo offlineRequestVo = new OfflineRequestVo();
        offlineRequestVo.setUID(message.getHeader().getUID());
        offlineMessageService.sendOfflineMessage(offlineRequestVo);
    }
}
