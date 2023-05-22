package com.example.service;

import com.example.common.constant.Constants;
import com.example.common.util.RedisUtil;
import com.example.storeapi.OfflineMessageSend;
import com.example.storeapi.vo.OfflineRequestVo;
import com.example.storeapi.vo.OfflineResponseVo;
import com.example.storeapi.vo.QueryResponseVo;
import com.example.util.ConvertToOfflineResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@DubboService
@Service
@Slf4j
public class OfflineMessageSendImpl implements OfflineMessageSend {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    QueryMessageService queryMessageService;
    @Override
    public List<OfflineResponseVo> getOfflineMessage(OfflineRequestVo offlineRequestVo) {

        //到mysql中查询最新消息id
        List<QueryResponseVo> latestMessageId = queryMessageService.getLatestMessageId(offlineRequestVo.getUID());

        List<OfflineResponseVo> list = new ArrayList<>();
        for(QueryResponseVo v : latestMessageId){
            if(v.getType() == Constants.CommandType.P2P_MSG){
                Set<String> p2PMessage = redisUtil.getP2PMessage(v.getID(), v.getUID(), v.getMID());
                for(String str : p2PMessage){
                    OfflineResponseVo parse = ConvertToOfflineResponseVo.parse(str);
                    parse.setType(Constants.CommandType.P2P_MSG);
                    parse.setUID(v.getUID());
                    parse.setID(v.getID());
                    list.add(parse);
                }
            }
            if(v.getType() == Constants.CommandType.GROUP_MSG){
                Set<String> groupMessage = redisUtil.getGroupMessage(v.getUID(), v.getID(), v.getMID());
                for(String str : groupMessage){
                    OfflineResponseVo parse = ConvertToOfflineResponseVo.parse(str);
                    parse.setType(Constants.CommandType.GROUP_MSG);
                    parse.setUID(v.getUID());
                    parse.setID(v.getID());
                    parse.setTID(offlineRequestVo.getUID());
                    list.add(parse);
                }
            }
        }
        log.info("离线消息查询成功：{}", list);
        return list;
    }
}
