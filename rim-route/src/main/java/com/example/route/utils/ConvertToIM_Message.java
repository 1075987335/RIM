package com.example.route.utils;

import com.example.client.request.SendMessageVo;
import com.example.client.response.AckVo;
import com.example.common.proto.Header;
import com.example.common.proto.IM_Message;

public class ConvertToIM_Message {

    public static IM_Message convert(SendMessageVo sendMessageVo, long TID) {
        IM_Message message = new IM_Message();
        Header header = new Header();
        message.setBody(sendMessageVo.getData());
        header.setTID(TID);
        header.setMID(sendMessageVo.getMID());
        header.setUID(sendMessageVo.getUID());
        header.setType(sendMessageVo.getType());
        header.setGID(sendMessageVo.getGID());
        message.setHeader(header);
        return message;
    }

    public static IM_Message convert(AckVo ackVo) {
        IM_Message message = new IM_Message();
        Header header = new Header();
        header.setType(ackVo.getType());
        header.setUID(ackVo.getUID());
        header.setTID(ackVo.getTID());
        header.setMID(ackVo.getMID());
        header.setGID(ackVo.getGID());
        message.setHeader(header);
        return message;
    }
}
