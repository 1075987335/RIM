package com.example.common.codec;

import com.example.common.serializer.Serializer;
import com.example.common.serializer.kyro.KryoSerializer;
import com.example.common.constant.Constants;
import com.example.common.proto.Header;
import com.example.common.proto.IM_Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MessageEncode extends MessageToByteEncoder<IM_Message> {

    Serializer serializer = new KryoSerializer();

    @Override
    protected void encode(ChannelHandlerContext ctx, IM_Message msg, ByteBuf byteBuf) throws Exception {
        if (msg != null) {
            Header header = msg.getHeader();
            Object body = msg.getBody();
            byte type = header.getType();
            byte message_type = header.getMessageType();
            byte[] headers = serializer.serialize(header);
            byte[] bodys = null;

            int totalLength = 0;
            int header_length = headers.length;
            int body_length = 0;

            if (body != null) {
                bodys = serializer.serialize(body);
                body_length = bodys.length;
            }

            //写入消息标志位
            byteBuf.writeInt(Constants.Code.MESSAGE_SYMBLE);

            if (type == Constants.CommandType.P2P_ACK || type == Constants.CommandType.GROUP_ACK) {
                totalLength = 9;
                byteBuf.writeInt(totalLength);
                byteBuf.writeByte(type);
                byteBuf.writeLong(header.getMID());
            } else if (type == Constants.CommandType.LOGIN) {
                totalLength = 9;
                byteBuf.writeInt(totalLength);
                byteBuf.writeByte(type);
                byteBuf.writeLong(header.getUID());
            } else if (type == Constants.CommandType.PING || type == Constants.CommandType.PONG) {
                totalLength = 1;
                byteBuf.writeInt(totalLength);
                byteBuf.writeByte(type);
            } else if (type == Constants.CommandType.P2P_MSG || type == Constants.CommandType.GROUP_MSG) {
                totalLength = header_length + body_length + 10;
                byteBuf.writeInt(totalLength);
                byteBuf.writeByte(type);
                byteBuf.writeInt(header_length);
                byteBuf.writeInt(body_length);
                byteBuf.writeByte(message_type);
                byteBuf.writeBytes(headers);
                byteBuf.writeBytes(bodys);
            }
        }
    }
}
