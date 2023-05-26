package com.example.route.service;

import com.example.MessageSendService;
import com.example.RouteUserService;
import com.example.client.request.SendMessageVo;
import com.example.client.response.AckVo;
import com.example.common.constant.Constants;
import com.example.common.proto.IM_Message;
import com.example.common.route.RouteInfo;
import com.example.common.util.RedisUtil;
import com.example.common.util.RouteInfoParseUtil;
import com.example.route.utils.DubboUtil;
import com.example.route.loadbalance.consistenthash.ConsistentHashHandle;
import com.example.route.mq.MQSend;
import com.example.route.service.strategy.send.MessageSendContext;
import com.example.route.thread.SendMessageJob;
import com.example.route.utils.ConvertToIM_Message;
import com.example.server.SendMessage;
import com.example.server.reqeust.SendRequestVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadPoolExecutor;

@Service
@DubboService
@Slf4j(topic = "MessageSendService")
public class MessageSendServiceImpl implements MessageSendService {

    @Autowired
    ConsistentHashHandle handle;

    @Autowired
    MQSend mqSend;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    BeforeSendService beforeSendService;

    @Autowired
    RouteUserService userService;

    @Autowired
    ThreadPoolExecutor sendJobExecutor;

    @Autowired
    MessageSendContext messageSendContext;

    /**
     * 寻找服务器发送消息
     * @param sendMessageVo
     */
    @Override
    public void sendMessageToServer(SendMessageVo sendMessageVo) {
        //幂等性检查
        if(checkDuplicate(sendMessageVo)){
            log.info("消息去重操作...");
            return;
        }
        messageSendContext.handle(sendMessageVo);
    }

    /**
     * 发送离线消息
     * @param sendMessageVo
     */
    public void sendOfflineMessage(SendMessageVo sendMessageVo){
        //幂等性检查
        if(checkDuplicate(sendMessageVo)){
            log.info("消息已处理过，此次请求无效！");
            return;
        }

        //单聊查看用户登陆状态
        if (!userService.getUserLoginState(sendMessageVo.getTID())) {
            log.info("用户{}已离线，无法发送离线消息...",sendMessageVo.getTID());
            return;
        }

        //加入到ACK等待列表中
        beforeSendService.addToAckList(sendMessageVo, sendMessageVo.getTID());

        //发送消息
        sendJobExecutor.execute(new SendMessageJob(sendMessageVo, sendMessageVo.getTID()));
    }
    /**
     * 在redis中保存离线消息
     * @param sendMessageVo
     */
    @Override
    public void storeOfflineMessage(SendMessageVo sendMessageVo) {
        IM_Message message = ConvertToIM_Message.convert(sendMessageVo, sendMessageVo.getTID());

        //加入到ACK确认列表
        beforeSendService.addToAckList(sendMessageVo, sendMessageVo.getTID());
        mqSend.sendMessage(message, true);
    }

    /**
     * 幂等性检查，查看消息是不是已经处理过了
     * @param sendMessageVo
     * @return
     */
    public boolean checkDuplicate(SendMessageVo sendMessageVo){
        byte type = sendMessageVo.getType();
        boolean exist = false;
        if(type == Constants.CommandType.P2P_MSG){
            exist = redisUtil.isP2PMessageExist(sendMessageVo.getMID());
        }
        else if(type == Constants.CommandType.GROUP_MSG){
            exist = redisUtil.isGroupmessageExist(sendMessageVo.getMID(), sendMessageVo.getGID());
        }
        return exist;
    }

    /**
     * 给客户端返回ack
     * @param ackVo
     */
    @Override
    public void sendAckToClient(AckVo ackVo) {
        //在redis找一台服务器进行路由
        String server = redisUtil.getUserRoute(ackVo.getTID());
        log.info("server：{}",server);
        RouteInfo info= RouteInfoParseUtil.parse(server);

        SendRequestVo sendRequestVo = new SendRequestVo();
        sendRequestVo.setTID(ackVo.getTID());
        sendRequestVo.setMID(ackVo.getMID());
        sendRequestVo.setType(ackVo.getType());
        Object[] parameters=new Object[]{sendRequestVo};
        DubboUtil.getDubboService(SendMessage.class,info.getIp(), info.getDubboPort(),"SendMessage", parameters);
        log.info("消息：{} ACK消息发送成功！", ackVo.getMID());
    }

    /**
     * 客户端向存储服务发送ack
     * @param ackVo
     */
    @Override
    public void sendAckToStoreService(AckVo ackVo) {
        IM_Message parse = ConvertToIM_Message.convert(ackVo);

        mqSend.sendMessage(parse, false);
    }

    /**
     * 重新寻找服务器发送消息
     * @param sendMessageVo
     */
    @Override
    public void sendRetryMessageToServer(SendMessageVo sendMessageVo) {
        sendJobExecutor.execute(new SendMessageJob(sendMessageVo, sendMessageVo.getTID()));
    }
}
