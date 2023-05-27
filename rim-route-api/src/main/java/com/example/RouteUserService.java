package com.example;


import com.example.client.request.LoginVO;
import com.example.client.request.OfflineReqVO;
import com.example.client.response.ServerInfo;

public interface RouteUserService {

    ServerInfo Login(LoginVO loginVO);

    void offLine(OfflineReqVO offlineReqVO);

    boolean getUserLoginState(long UID);

}
