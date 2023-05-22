package com.example.storeapi;

import com.example.storeapi.vo.OfflineRequestVo;
import com.example.storeapi.vo.OfflineResponseVo;

import java.util.List;

public interface OfflineMessageSend {

    List<OfflineResponseVo> getOfflineMessage(OfflineRequestVo offlineRequestVo);
}
