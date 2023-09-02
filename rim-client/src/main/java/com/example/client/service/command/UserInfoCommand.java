package com.example.client.service.command;

import com.example.client.config.UserInfo;
import com.example.client.service.InnerCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserInfoCommand implements InnerCommand {

    @Autowired
    UserInfo userInfo;

    @Override
    public void process(String msg) {
        log.info("当前登陆用户为：{}号", userInfo.getUserID());
    }
}
