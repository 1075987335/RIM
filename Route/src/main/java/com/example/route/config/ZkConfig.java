package com.example.route.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class ZkConfig {

    @Value("${zk.addr}")
    private String zkAddr;

    @Value("${zk.root}")
    private String zkRoot;
}
