package com.example.route.service;

import com.example.RouteUserService;
import com.example.client.request.LoginVO;
import com.example.client.request.OfflineReqVO;
import com.example.client.response.ServerInfo;
import com.example.common.route.RouteInfo;
import com.example.common.util.RedisUtil;
import com.example.common.util.RouteInfoParseUtil;
import com.example.route.loadbalance.consistenthash.ConsistentHashHandle;
import com.example.route.zk.ZKit;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@DubboService
@Service
@Slf4j(topic = "UserServiceImpl")
public class UserServiceImpl implements RouteUserService {

    @Autowired
    ZKit zKit;

    @Autowired
    ConsistentHashHandle handle;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public ServerInfo Login(LoginVO loginVO) {
        List<String> serverList = zKit.getAllNode();
        if(serverList == null || serverList.isEmpty()){
            log.info("当前无路由服务器可选！");
            return null;
        }
        String server=handle.routeServer(serverList,String.valueOf(loginVO.getUID()));

        RouteInfo route= RouteInfoParseUtil.parse(server);
        ServerInfo serverInfo=new ServerInfo();
        serverInfo.setIp(route.getIp());
        serverInfo.setServerPort(route.getServerPort());
        serverInfo.setHttpPort(route.getHttpPort());

        //对路由进行缓存操作
        redisUtil.setUserRoute(server, loginVO.getUID());
        log.info("用户{}路由缓存成功！", loginVO.getUID());

        //更新用户登陆状态
        redisUtil.setUserLoginState(loginVO.getUID(),1);
        return serverInfo;
    }

    @Override
    public void offLine(OfflineReqVO offlineReqVO) {
        if (redisUtil.offlineRedisDelete(offlineReqVO.getUID())) {
            log.info("用户{}离线成功！",offlineReqVO.getUID());
        }
        else
            log.error("用户{}离线失败！",offlineReqVO.getUID());
    }

    @Override
    public boolean getUserLoginState(long TID) {
        return redisUtil.getUserLoginState(TID);
    }
}
