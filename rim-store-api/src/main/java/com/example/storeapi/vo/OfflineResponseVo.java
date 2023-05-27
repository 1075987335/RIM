package com.example.storeapi.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class OfflineResponseVo implements Serializable {
    long UID;

    long TID;

    long MID;

    long GID;

    byte type;

    Object data;
}
