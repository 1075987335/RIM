package com.example.common.route;

import lombok.Data;

@Data
public class RouteInfo {

    private String ip;

    private int ServerPort;

    private int httpPort;

    private int dubboPort;

    public RouteInfo(String ip, Integer cimServerPort, Integer httpPort, Integer dubboPort) {
        this.ip = ip;
        this.ServerPort = cimServerPort;
        this.httpPort = httpPort;
        this.dubboPort=dubboPort;
    }
}
