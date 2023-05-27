package com.example.server.config;

import com.example.server.kit.ZKit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
@Slf4j(topic = "ShutDownHook")
public class ShutDownHook {

    @Autowired
    ServerConfig config;

    @Autowired
    ZKit zKit;
    public void clearAll(){
        log.info("添加钩子函数清除zookeeper中的注册信息");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                //zookeeper中删除这个地址
                String path = config.getZkRoot() +"/"+ InetAddress.getLocalHost().getHostAddress() + ":" +config.getServerPort() + ":" + config.getHttpPort() + ":" + config.getDubboPort();
                zKit.removeNode(path);
            } catch (UnknownHostException ignored) {
                System.out.println(ignored);
            }
        }));
    }
}
