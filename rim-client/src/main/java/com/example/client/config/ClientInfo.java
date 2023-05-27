package com.example.client.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Data
@Component
public class ClientInfo {

    @Value("${rim.client.port}")
    private int ClientPort;

    @Value("${server.port}")
    private int HttpPort;

    @Value("${rim.heartbeat.time}")
    private int HeartBeatTime;

    @Value("${rim.reconnect.count}")
    private int ReconnectCount;

    private boolean isNormalClose = false;
}
