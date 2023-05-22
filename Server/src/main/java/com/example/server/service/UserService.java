package com.example.server.service;

import com.example.RouteUserService;
import com.example.client.request.OfflineReqVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {

    @DubboReference
    RouteUserService userService;

    public void userOffline(long UID){
        OfflineReqVO offlineReqVO = new OfflineReqVO();
        offlineReqVO.setUID(UID);
        userService.offLine(offlineReqVO);
    }
}
