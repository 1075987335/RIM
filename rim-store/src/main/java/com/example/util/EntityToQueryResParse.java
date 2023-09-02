package com.example.util;

import com.example.common.constant.Constants;
import com.example.dao.UserFriendsEntity;
import com.example.dao.UserGroupsEntity;
import com.example.storeapi.vo.QueryResponseVo;

import java.util.ArrayList;
import java.util.List;

public class EntityToQueryResParse {

    public static List<QueryResponseVo> parse(List<UserGroupsEntity> userGroups, List<UserFriendsEntity> userFriends) {
        List<QueryResponseVo> list = new ArrayList<>();
        //群聊封装
        for (UserGroupsEntity e : userGroups) {
            QueryResponseVo vo = new QueryResponseVo();
            vo.setType(Constants.CommandType.GROUP_MSG);
            vo.setGID(e.getGroupId());
            vo.setMID(e.getLatestMessageId());
            vo.setTID(e.getUserId());
            list.add(vo);
        }

        //私聊封装
        for (UserFriendsEntity e : userFriends) {
            QueryResponseVo vo = new QueryResponseVo();
            vo.setType(Constants.CommandType.P2P_MSG);
            vo.setUID(e.getFriendId());
            vo.setTID(e.getUserId());
            vo.setMID(e.getLatestMessageId());
            list.add(vo);
        }
        return list;
    }
}
