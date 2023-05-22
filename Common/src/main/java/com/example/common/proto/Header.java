package com.example.common.proto;

import com.example.common.enums.MessageType;
import lombok.Data;

@Data
public class Header {

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
     * 4字节 协议序列化类型
     */
    private byte messageType= MessageType.DATA_TYPE_KRYO.getCode();

}
