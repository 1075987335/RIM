package com.example.common.codec;

import com.example.common.constant.Constants;
import com.example.common.enums.MessageType;
import com.example.common.proto.Header;
import com.example.common.proto.IM_Message;
import com.example.common.serializer.Serializer;
import com.example.common.serializer.kyro.KryoSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class MessageDecode extends ByteToMessageDecoder {

    int BASE_LENGTH = 8;

    Serializer serializer = new KryoSerializer();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> list) throws Exception {
        if (in.readableBytes() >= BASE_LENGTH) {

            //记录消息包开始的index
            int beginIndex = 0;
            while (true) {
                //获取包头开始的index
                beginIndex = in.readerIndex();

                //标记包头开始的index
                in.markReaderIndex();

                //读到协议的开始标志就结束while循环
                if (in.readInt() == Constants.Code.MESSAGE_SYMBLE) {
                    break;
                }

                //如果读取包头失败了，就重新返回到原来开始的地方，往后移动一个字节继续判断
                in.resetReaderIndex();
                in.readByte();

                //如果剩余的字节长度比基本长度小，就退出，等待后续的数据到达
                if (in.readableBytes() < BASE_LENGTH) {
                    return;
                }
            }

            //获取消息的总长度
            int total_length = in.readInt();
            //看数据是不是都到齐了
            if (in.readableBytes() < total_length) {
                in.readerIndex(beginIndex);
                return;
            }

            //获取数据类型
            byte type = in.readByte();

            IM_Message message = new IM_Message();
            Header header = null;
            Object body = null;

            if (type == Constants.CommandType.P2P_ACK || type == Constants.CommandType.GROUP_ACK) {
                long msgId = in.readLong();
                header = new Header();
                header.setType(type);
                header.setMID(msgId);
            } else if (type == Constants.CommandType.PING || type == Constants.CommandType.PONG) {
                header = new Header();
                header.setType(type);
            } else if (type == Constants.CommandType.LOGIN) {
                long userId = in.readLong();
                header = new Header();
                header.setType(type);
                header.setUID(userId);
            } else if (type == Constants.CommandType.P2P_MSG || type == Constants.CommandType.GROUP_MSG) {
                int header_length = in.readInt();
                int body_length = in.readInt();
                byte message_type = in.readByte();
                byte[] headers = new byte[header_length];
                byte[] bodys = new byte[body_length];
                in.readBytes(headers);
                in.readBytes(bodys);

                if (message_type == MessageType.DATA_TYPE_KRYO.getCode()) {
                    header = serializer.deserialize(headers, Header.class);
                    body = serializer.deserialize(bodys, String.class);
                }
            }

            //封装消息
            message.setHeader(header);
            message.setBody(body);
            list.add(message);
        }
    }
}
