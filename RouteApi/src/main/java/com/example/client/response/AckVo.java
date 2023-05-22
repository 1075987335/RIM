package com.example.client.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class AckVo implements Serializable {
    long MID;

    long UID;

    long TID;

    long GID;

    byte type;
}
