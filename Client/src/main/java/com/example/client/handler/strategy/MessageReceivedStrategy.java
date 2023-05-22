package com.example.client.handler.strategy;

import com.example.common.proto.IM_Message;

public interface MessageReceivedStrategy {

    void handle(IM_Message message);
}
