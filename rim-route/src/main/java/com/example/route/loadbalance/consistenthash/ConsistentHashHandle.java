package com.example.route.loadbalance.consistenthash;


import com.example.route.loadbalance.RouteHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConsistentHashHandle implements RouteHandle {

    @Autowired
    private AbstractConsistentHash hash;

    private final Object LOCK = new Object();

    @Override
    public String routeServer(List<String> values, String key) {
        String process;
        synchronized (LOCK) {
            hash.clear();
            process = hash.process(values, key);
        }
        return process;
    }

    /**
     * 初始化哈希环
     *
     * @param values
     */
    @Override
    public void init(List<String> values) {
        hash.init(values);
    }
}
