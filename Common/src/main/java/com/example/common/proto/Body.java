package com.example.common.proto;

import lombok.Data;

@Data
public class Body<T>{


    /**
     * 实际传输数据
     */
    private T data;

}
