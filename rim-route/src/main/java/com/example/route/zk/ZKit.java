package com.example.route.zk;

import com.example.route.config.ZkConfig;
import com.example.route.zk.listener.ZKChangeListener;
import com.github.benmanes.caffeine.cache.Cache;
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

    @Autowired
    private ZkClient zkClient;

    @Autowired
    private Cache<String, Object> caffeineCache;


    public List<String> getAllNode() {
        List<String> children;
        synchronized (Cache.class) {
            children = (List<String>) caffeineCache.asMap().get(zkConfig.getZkRoot());
            if (children == null) {
                children = zkClient.getChildren(zkConfig.getZkRoot());
                caffeineCache.put(zkConfig.getZkRoot(), children);
                subscribeChildChanges();
                log.info("未走缓存...");
            }
        }
        log.info("查询到的节点：[{}]", children);
        return children;
    }

    public void subscribeChildChanges() {
        zkClient.subscribeChildChanges(zkConfig.getZkRoot(), new ZKChangeListener(caffeineCache, zkClient, zkConfig));

    }
}
