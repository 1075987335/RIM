package com.example.server.reqeust;

import lombok.Data;

import java.io.Serializable;

@Data
public class SendRequestVo implements Serializable {

    private long UID;

    private long TID;

    private byte type;

    private long MID;

    private long GID;

    private Object body;
}
