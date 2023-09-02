package com.example.client.util;

import com.example.client.response.AckVo;
import com.example.common.proto.Header;
import com.example.common.proto.IM_Message;

public class ConvertToAckVo {

    public static AckVo convert(IM_Message message, byte type) {
        Header header = message.getHeader();
        AckVo ackVo = new AckVo();
        ackVo.setTID(header.getTID());
        ackVo.setMID(header.getMID());
        ackVo.setUID(header.getUID());
        ackVo.setGID(header.getGID());
        ackVo.setType(type);
        return ackVo;
    }
}
