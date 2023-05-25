package com.example.storeapi.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class QueryResponseVo implements Serializable {

    byte type;

    long TID;

    long GID;

    long UID;

    long MID;
}
