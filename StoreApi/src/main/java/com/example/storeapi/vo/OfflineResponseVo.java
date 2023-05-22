package com.example.storeapi.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class OfflineResponseVo implements Serializable {
    long UID;

    long ID;

    long TID;

    long MID;

    byte type;

    Object data;
}
