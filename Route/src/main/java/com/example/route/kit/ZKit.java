package com.example.route.kit;

import com.example.route.config.ZkConfig;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j(topic = "ZKit")
public class ZKit {

    @Autowired
    private ZkConfig zkConfig;

    private ZkClient zkClient;

    public List<String> getAllNode(){
        createZkClient();
        List<String> children = zkClient.getChildren(zkConfig.getZkRoot());
        log.info("查询到的节点：[{}]", children.toString());
        return children;
    }

    public void createZkClient(){
        if(zkClient==null){
            zkClient=new ZkClient(zkConfig.getZkAddr(),Integer.MAX_VALUE);
        }
    }
}
