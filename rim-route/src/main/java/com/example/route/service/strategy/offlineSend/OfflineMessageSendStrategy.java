package com.example.route.service.strategy.offlineSend;

import com.example.client.request.SendMessageVo;

public interface OfflineMessageSendStrategy {

    void process(SendMessageVo sendMessageVo);
}
