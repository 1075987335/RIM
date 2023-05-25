package com.example.client.service.command;

import com.example.client.client.RIMClient;
import com.example.client.config.UserInfo;
import com.example.client.service.InnerCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j(topic = "LoginCommand")
public class LoginCommand implements InnerCommand {

    @Autowired
    UserInfo userInfo;

    @Autowired
    RIMClient rimClient;

    @Override
    public void process(String msg) {
        if(msg.split(" ").length != 2){
            log.info("incorrect command, :login [userID]");
            return;
        }
        String[] str = msg.split(" ");
        long UID = Long.parseLong(str[1]);
        userInfo.setUserID(UID);
        rimClient.start();
    }
}
