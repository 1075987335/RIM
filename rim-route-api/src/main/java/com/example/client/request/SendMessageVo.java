package com.example.client.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class SendMessageVo implements Serializable {
    /**
     * 用户ID
     */
    private long UID;

    /**
     * 发送方ID
     */
    private long TID;

    /**
     * 消息ID
     */
    private long MID;

    /**
     * 群组ID
     */
    private long GID;

    /**
     * 4字节 消息操作指令,也包括消息类型
     */
    private byte type;

    /**
     * 实际传输的数据
     */
    Object data;
}
