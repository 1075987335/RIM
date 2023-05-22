package com.example.client.scanner;

import com.example.client.service.MessageHandler;
import com.example.client.util.SpringBeanFactory;

import java.util.Scanner;

public class Scan implements Runnable{
    private MessageHandler msgHandler;

    public Scan(){
        msgHandler= SpringBeanFactory.getBean(MessageHandler.class);
    }
    @Override
    public void run() {
        Scanner sc=new Scanner(System.in);
        while(true){
            String msg=sc.nextLine();
            msgHandler.handler(msg);
        }
    }
}
