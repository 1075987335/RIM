package com.example.client.service.message;

import com.example.client.service.command.CommandFactory;
import com.example.client.service.InnerCommand;
import com.example.client.service.MessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageHandlerImpl implements MessageHandler {

    @Autowired
    CommandFactory commandFactory;
    @Override
    public void handler(String msg) {
        InnerCommand instance=commandFactory.getInstance(msg);
        if (instance != null) {
            instance.process(msg);
        }
    }
}
