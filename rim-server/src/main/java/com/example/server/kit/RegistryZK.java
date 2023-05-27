package com.example.server.kit;

import com.example.server.config.ServerConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RegistryZK implements Runnable{

    private ZKit zKit;

    private ServerConfig config;

    private String ip;

    public RegistryZK(String ip, ZKit zKit, ServerConfig config) {
        this.ip = ip;
        this.config = config;
        this.zKit =zKit;
    }

    @Override
    public void run() {
        //创建父节点
        zKit.createRootNode();
        //是否要将自己注册到 ZK
        if (config.isZkSwith()){
            String path = config.getZkRoot() +"/"+ ip + ":" +config.getServerPort() + ":" + config.getHttpPort() + ":" + config.getDubboPort();
            zKit.createNode(path);
            log.info("Registry zookeeper success, msg=["+path+"]");
        }
    }
}
