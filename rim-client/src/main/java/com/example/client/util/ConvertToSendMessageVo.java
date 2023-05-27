package com.example.client.util;

import com.example.client.config.UserInfo;
import com.example.client.request.SendMessageVo;
import com.example.common.constant.Constants;

public class ConvertToSendMessageVo {

    public static SendMessageVo convert(String[] str, UserInfo userInfo, long id, byte type){
        SendMessageVo sendMessageVo = new SendMessageVo();
        sendMessageVo.setType(type);
        sendMessageVo.setData(str[2]);
        sendMessageVo.setUID(userInfo.getUserID());
        sendMessageVo.setMID(id);
        if(type == Constants.CommandType.P2P_MSG){
            sendMessageVo.setTID(Long.parseLong(str[1]));
        }
        else if(type == Constants.CommandType.GROUP_MSG){
            sendMessageVo.setGID(Long.parseLong(str[1]));
        }
        return sendMessageVo;
    }
}
