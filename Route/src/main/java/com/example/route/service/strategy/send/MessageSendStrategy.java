package com.example.route.service.strategy.send;

import com.example.client.request.SendMessageVo;

public interface MessageSendStrategy {

    void process(SendMessageVo sendMessageVo);
}
