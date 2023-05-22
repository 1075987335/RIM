package com.example.client.service;

import com.example.client.request.LoginVO;
import com.example.client.response.ServerInfo;

public interface UserService {

    /**
     * 登陆操作，路由服务器
     * @param loginVO
     * @return
     */
    ServerInfo login(LoginVO loginVO);

    /**
     * 向服务器注册，让服务器保存channel信息
     */
    void loginServer();

    /**
     * 用户离线，离线操作集中给路由层来做
     */
    void offline();
}
