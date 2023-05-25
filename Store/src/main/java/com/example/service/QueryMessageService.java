package com.example.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.dao.UserFriendsEntity;
import com.example.dao.UserGroupsEntity;
import com.example.mapper.GroupHistoryMapper;
import com.example.mapper.P2PHistoryMapper;
import com.example.mapper.UserFriendsMapper;
import com.example.mapper.UserGroupsMapper;
import com.example.storeapi.vo.QueryResponseVo;
import com.example.util.EntityToQueryResParse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j(topic = "QueryMessageService")
public class QueryMessageService{
    @Autowired
    P2PHistoryMapper p2pHistoryMapper;

    @Autowired
    UserFriendsMapper userFriendsMapper;

    @Autowired
    GroupHistoryMapper groupHistoryMapper;

    @Autowired
    UserGroupsMapper userGroupsMapper;

    /**
     * 从群聊和私聊中获取最新消息ID以及目标等信息
     * @param UID
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public List<QueryResponseVo> getLatestMessageId(long UID) {
        //获取单聊的最新消息ID
        QueryWrapper<UserFriendsEntity> wrapperP2P = new QueryWrapper();
        wrapperP2P.eq("user_id", UID);
        List<UserFriendsEntity> userFriends = userFriendsMapper.selectList(wrapperP2P);

        //获取群聊的最新消息ID
        QueryWrapper<UserGroupsEntity> wrapperGroup = new QueryWrapper<>();
        wrapperGroup.eq("user_id", UID);
        List<UserGroupsEntity> userGroups = userGroupsMapper.selectList(wrapperGroup);

        List<QueryResponseVo> parse = EntityToQueryResParse.parse(userGroups, userFriends);
        log.info("刚查询出来的：{}", parse);
        return parse;
    }
}
