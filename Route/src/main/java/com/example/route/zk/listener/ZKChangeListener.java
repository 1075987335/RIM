package com.example.route.zk.listener;

import com.example.route.config.ZkConfig;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;

import java.util.List;

@Slf4j
public class ZKChangeListener implements IZkChildListener {

    Cache<String, Object> cache;

    private ZkClient zkClient;

    private ZkConfig zkConfig;

    public ZKChangeListener(Cache<String, Object> cache, ZkClient zkClient, ZkConfig zkConfig){
        this.cache = cache;
        this.zkClient = zkClient;
        this.zkConfig = zkConfig;
    }

    @Override
    public void handleChildChange(String s, List<String> list) {
        //重新缓存缓存数据，加一个粒度很大的锁
        synchronized (Cache.class){
            cache.put(s, zkClient.getChildren(zkConfig.getZkRoot()));
            log.info("更新本地缓存成功！");
        }
        log.info("监听到zookeeper变化！path：{}, changes：{}", s, list);
    }
}
