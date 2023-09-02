package com.example.server.kit;

import com.example.server.config.ServerConfig;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ZKit {


    private ZkClient zkClient;

    @Autowired
    private ServerConfig appConfiguration;


    /**
     * 创建父级节点
     */
    public void createRootNode() {
        createZkClient();
        boolean exists = zkClient.exists(appConfiguration.getZkRoot());
        if (exists) {
            return;
        }
        //创建 root
        zkClient.createPersistent(appConfiguration.getZkRoot());
    }

    /**
     * 写入指定节点 临时目录
     *
     * @param path
     */
    public void createNode(String path) {
        createZkClient();
        zkClient.createEphemeral(path);
    }

    /**
     * 删除指定节点
     *
     * @param path
     * @return
     */
    public boolean removeNode(String path) {
        return zkClient.delete(path);
    }

    public void createZkClient() {
        if (zkClient == null)
            zkClient = new ZkClient(appConfiguration.getZkAddr(), Integer.MAX_VALUE);
    }

}
