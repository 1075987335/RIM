package com.example.client.config;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ClientState {

    /**
     * 客户端状态
     * command = true代表是用户主动退出
     * command = false代表异常退出
     */
    private boolean isNormalClose = false;

    private boolean isReconnect = false;
}
