package com.example.route.loadbalance.consistenthash;


import com.example.route.loadbalance.RouteHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2019-02-27 00:33
 * @since JDK 1.8
 */
@Component
public class ConsistentHashHandle implements RouteHandle {

    @Autowired
    private AbstractConsistentHash hash;

    @Override
    public String routeServer(List<String> values, String key) {
        hash.clear();
        return hash.process(values, key);
    }
}
