package com.example.common.proto;

import lombok.Data;

@Data
public class IM_Message {

    /**
     * 消息头：43字节
     */
    private Header header;

    /**
     * 消息体
     */
    private Object body;
}
