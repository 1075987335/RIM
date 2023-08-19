package com.example.route.loadbalance;

import java.util.List;


public interface RouteHandle {

    /**
     * 在一批服务器里进行路由
     * @param values
     * @param key
     * @return
     */
    String routeServer(List<String> values,String key) ;

    void init(List<String> values);
}
