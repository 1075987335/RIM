package com.example.client.service.command;

import com.example.client.service.InnerCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class P2PSendTestCommand implements InnerCommand {

    @Autowired
    P2PMsgCommand p2PMsgCommand;

    @Override
    public void process(String msg) {
        String[] str = msg.split(" ");
        int count = Integer.parseInt(str[1]);
        for(int i = 0; i < count; i++){
            String s = ":p2p 2 " + i;
            p2PMsgCommand.process(s);
        }

    }
}
