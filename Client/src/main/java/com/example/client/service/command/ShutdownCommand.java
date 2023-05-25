package com.example.client.service.command;

import com.example.client.client.RIMClient;
import com.example.client.config.ClientState;
import com.example.client.service.InnerCommand;
import com.example.client.thread.duplicate.ReceivedMessage;
import com.example.common.util.RingBufferWheel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledThreadPoolExecutor;

@Service
@Slf4j
public class ShutdownCommand implements InnerCommand {
    @Autowired
    RIMClient client;

    @Autowired
    RingBufferWheel ringBufferWheel;

    @Autowired
    ClientState clientState;

    @Autowired
    ScheduledThreadPoolExecutor scheduleJobExecutor;

    @Autowired
    ReceivedMessage receivedMessage;

    @Override
    public void process(String msg) {
        if(!msg.equals(":q")){
            log.info("incorrect command, :q");
            return;
        }
        clientState.setNormalClose(true);
        client.close();
        receivedMessage.clear();
    }
}
