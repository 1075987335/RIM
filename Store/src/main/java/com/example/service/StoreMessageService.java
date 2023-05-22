package com.example.service;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.common.proto.Header;
import com.example.common.proto.IM_Message;
import com.example.common.util.RedisUtil;
import com.example.dao.GroupMessageEntity;
import com.example.dao.P2PMessageEntity;
import com.example.mapper.GroupHistoryMapper;
import com.example.mapper.P2PHistoryMapper;
import com.example.mapper.UserFriendsMapper;
import com.example.mapper.UserGroupsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j(topic = "StoreMessageService")
public class StoreMessageService {

    @Autowired
    P2PHistoryMapper p2pHistoryMapper;

    @Autowired
    UserFriendsMapper userFriendsMapper;

    @Autowired
    GroupHistoryMapper groupHistoryMapper;

    @Autowired
    UserGroupsMapper userGroupsMapper;

    @Autowired
    RedisUtil redisUtil;

    /**
     * 保存单聊消息，更新最新接收消息id
     * @param msg
     */
    @Transactional(rollbackFor = Exception.class)
    public void doStoreP2PMessage(IM_Message msg){
        P2PMessageEntity storemessage=new P2PMessageEntity();
        Header header=msg.getHeader();
        Object data=msg.getBody();
        storemessage.setBody((String)data);
        storemessage.setMessageId(header.getMID());
        storemessage.setFromUser(header.getUID());
        storemessage.setToUser(header.getTID());
        storemessage.setState(0);
        try {
            //消息持久化在mysql中
            p2pHistoryMapper.insert(storemessage);
            log.info("单聊消息Mysql持久化成功！");
            //redis中生成对应的用于去重的消息
            redisUtil.cacheP2PMessage(header.getMID(), 15L);
            log.info("单聊去重消息已存储在Redis中！");
        } catch (Exception e) {
            log.info("单聊消息持久化失败！");
        }
    }

    /**
     * 在redis中保存单聊离线消息
     * @param message
     */
    @Transactional(rollbackFor = Exception.class)
    public void doStoreP2POfflineMessage(IM_Message message){
        Header header = new Header();
        try {
            redisUtil.setP2PMessage(header.getUID(), header.getTID(), header.getMID(), (String)message.getBody());
            log.info("单聊离线消息:[{}]成功保存在redis中");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 保存群聊消息，更新最新接收消息id
     * @param msg
     */
    @Transactional(rollbackFor = Exception.class)
    public void doStoreGroupMessage(IM_Message msg){
        GroupMessageEntity groupMessageEntity=new GroupMessageEntity();
        Header header=msg.getHeader();
        groupMessageEntity.setGroupId(header.getGID());
        groupMessageEntity.setFromUser(header.getUID());
        groupMessageEntity.setMessageId(header.getMID());
        groupMessageEntity.setBody((String)msg.getBody());
        try {

            groupHistoryMapper.insert(groupMessageEntity);
            log.info("群聊消息Mysql持久化成功！");

            redisUtil.setGroupMessage(header.getUID(), header.getGID(), header.getMID(), (String)msg.getBody());
            log.info("群聊消息Redis持久化成功！");
        } catch (Exception e) {
            log.info("群聊消息持久化失败！");
        }
    }

    /**
     * 接收单聊ACK
     */
    @Transactional(rollbackFor = Exception.class)
    public void doStoreP2PAck(IM_Message msg){
        //更新消息接收状态
        Header header=msg.getHeader();
        UpdateWrapper wrapper=new UpdateWrapper();
        wrapper.eq("to_user",header.getTID());
        wrapper.eq("from_user",header.getUID());
        wrapper.set("state",1);

        //更新最新接收消息id
        UpdateWrapper updateWrapper=new UpdateWrapper<>();
        updateWrapper.eq("user_id",header.getTID());
        updateWrapper.eq("friend_id",header.getUID());
        updateWrapper.set("latest_message_id",header.getMID());
        try {
            //更新消息记录中的消息接收状态
            p2pHistoryMapper.update(null,wrapper);
            //更新用户接收最新的消息ID
            userFriendsMapper.update(null,updateWrapper);
            log.info("单聊Mysql接收ack成功！");

            //删除已接收的在redis中的离线消息
            redisUtil.deleteP2PMessage(header.getUID(), header.getTID(), header.getMID());
        } catch (Exception e) {
            log.info("单聊接收ack失败！");
            throw new RuntimeException();
        }
    }

    /**
     * 接收群聊ACK
     */
    @Transactional(rollbackFor = Exception.class)
    public void doStoreGroupACK(IM_Message msg){
        Header header=msg.getHeader();

        //更新最新接收消息id
        UpdateWrapper updateWrapper=new UpdateWrapper();
        updateWrapper.eq("user_id",header.getUID());
        updateWrapper.eq("group_id",header.getGID());
        updateWrapper.set("latest_message_id",header.getMID());
        try {
            userGroupsMapper.update(null,updateWrapper);
            log.info("群聊ACK处理成功！");
        } catch (Exception e) {
            log.info("群聊ACK处理失败！");
            throw new RuntimeException(e);
        }
    }
}
