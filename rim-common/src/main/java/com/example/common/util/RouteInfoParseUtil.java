package com.example.common.util;

import com.example.common.route.RouteInfo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RouteInfoParseUtil {

    public static RouteInfo parse(String info) {
        try {
            String[] serverInfo = info.split(":");
            RouteInfo routeInfo = new RouteInfo(serverInfo[0], Integer.parseInt(serverInfo[1]), Integer.parseInt(serverInfo[2]), Integer.parseInt(serverInfo[3]));
            return routeInfo;
        } catch (Exception e) {
            log.info("路由信息转换错误！");
        }
        return null;
    }
}
