package com.example.server.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class ServerConfig {
    @Value("${app.zk.root}")
    private String zkRoot;

    @Value("${zk.addr}")
    private String zkAddr;

    @Value("${zk.switch}")
    private boolean zkSwith;

    @Value("${zk.connect.timeout}")
    private int zkConnectTimeOut;

    @Value("${rim.server.port}")
    private int serverPort;

    @Value("${server.port}")
    private String httpPort;

    @Value("${dubbo.protocol.port}")
    private int dubboPort;

    @Value("${rim.heatbeat.time}")
    private long heartBeatTime;
}
