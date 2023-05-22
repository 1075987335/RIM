package com.example.route.utils;

import com.example.client.request.SendMessageVo;
import com.example.common.constant.Constants;
import com.example.common.proto.Header;
import com.example.common.proto.IM_Message;
import com.example.storeapi.vo.OfflineResponseVo;

public class ConvertToSendMessageVo {

    public static SendMessageVo parse(IM_Message message){
        if (message != null) {
            Header header = message.getHeader();
            SendMessageVo sendMessageVo = new SendMessageVo();
            sendMessageVo.setMID(header.getMID());
            sendMessageVo.setType(header.getType());
            sendMessageVo.setData(message.getBody());
            sendMessageVo.setTID(header.getTID());
            sendMessageVo.setUID(header.getUID());
            sendMessageVo.setGID(header.getGID());
            return sendMessageVo;
        }
        return null;
    }

    public static SendMessageVo parse(OfflineResponseVo offlineResponseVo){
        if(offlineResponseVo != null){
            SendMessageVo sendMessageVo = new SendMessageVo();
            byte type = offlineResponseVo.getType();
            sendMessageVo.setMID(offlineResponseVo.getMID());
            sendMessageVo.setType(offlineResponseVo.getType());
            sendMessageVo.setData(offlineResponseVo.getData());
            if(type == Constants.CommandType.P2P_MSG){
                sendMessageVo.setUID(offlineResponseVo.getID());
                sendMessageVo.setTID(offlineResponseVo.getUID());
                sendMessageVo.setType(Constants.CommandType.P2P_MSG);
            }
            else if(type == Constants.CommandType.GROUP_MSG){
                sendMessageVo.setGID(offlineResponseVo.getID());
                sendMessageVo.setUID(offlineResponseVo.getUID());
                sendMessageVo.setTID(offlineResponseVo.getTID());
                sendMessageVo.setType(Constants.CommandType.GROUP_MSG);
            }
            return sendMessageVo;
        }
        return null;
    }
}
