package com.example.server;

import com.example.server.config.ServerConfig;
import com.example.server.kit.ZKit;
import com.example.server.kit.RegistryZK;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetAddress;

@SpringBootApplication
@EnableDubbo
public class ServerApplication implements CommandLineRunner {
    @Autowired
    ZKit zKit;

    @Autowired
    ServerConfig serverConfig;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ServerApplication.class,args);
    }

    @Override
    public void run(String... strings) throws Exception {
        String addr= InetAddress.getLocalHost().getHostAddress();
        Thread thread=new Thread(new RegistryZK(addr,zKit,serverConfig));
        thread.start();
    }
}
