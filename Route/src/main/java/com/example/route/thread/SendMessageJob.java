package com.example.route.thread;

import com.example.client.request.SendMessageVo;
import com.example.common.route.RouteInfo;
import com.example.common.util.RedisUtil;
import com.example.common.util.RouteInfoParseUtil;
import com.example.route.kit.DubboUtil;
import com.example.route.utils.SpringBeanFactory;
import com.example.server.SendMessage;
import com.example.server.reqeust.SendRequestVo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SendMessageJob implements Runnable{

    RedisUtil redisUtil;

    SendMessageVo sendMessageVo;

    long TID;

    public SendMessageJob(SendMessageVo sendMessageVo, long TID){
        this.sendMessageVo = sendMessageVo;
        this.TID = TID;
        redisUtil = SpringBeanFactory.getBean(RedisUtil.class);
    }
    @Override
    public void run() {
        //在redis找一台服务器进行路由
        String server = redisUtil.getUserRoute(TID);
        if(server == null){
            log.error("路由服务器为空，请检查发送的目标用户ID的正确性...");
            return;
        }
        log.info("server：{}",server);
        RouteInfo info= RouteInfoParseUtil.parse(server);

        log.info("SendMessageVo:{}", sendMessageVo);
        SendRequestVo sendRequestVo = new SendRequestVo();
        sendRequestVo.setTID(TID);
        sendRequestVo.setBody(sendMessageVo.getData());
        sendRequestVo.setUID(sendMessageVo.getUID());
        sendRequestVo.setMID(sendMessageVo.getMID());
        sendRequestVo.setType(sendMessageVo.getType());
        sendRequestVo.setGID(sendMessageVo.getGID());
        log.info("即将发送消息：{}", sendRequestVo);

        Object[] parameters=new Object[]{sendRequestVo};
        DubboUtil.getDubboService(SendMessage.class,info.getIp(), info.getDubboPort(),"SendMessage", parameters);
    }
}
