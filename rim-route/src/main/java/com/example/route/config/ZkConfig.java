package com.example.route.config;

import lombok.Data;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class ZkConfig {

    @Value("${zk.addr}")
    private String zkAddr;

    @Value("${zk.root}")
    private String zkRoot;

    @Bean("zkClient")
    public ZkClient zkClient() {
        return new ZkClient(zkAddr, Integer.MAX_VALUE);
    }
}
