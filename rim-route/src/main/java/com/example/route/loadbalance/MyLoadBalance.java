package com.example.route.loadbalance;


import com.example.route.loadbalance.consistenthash.ConsistentHashHandle;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.cluster.loadbalance.AbstractLoadBalance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MyLoadBalance extends AbstractLoadBalance {

    @Autowired
    ConsistentHashHandle handle;

    @Override
    protected <T> Invoker<T> doSelect(List<Invoker<T>> invokers, URL url, Invocation invocation) {
        invokers.get(0).getUrl().getHost();
        invokers.get(0).getUrl().getPort();
        return null;
    }
}
