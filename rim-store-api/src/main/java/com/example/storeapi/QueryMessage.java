package com.example.storeapi;

import com.example.storeapi.vo.QueryResponseVo;

import java.util.List;

public interface QueryMessage {

    public List<QueryResponseVo> getLatestMessageId(long UID);
}
