package com.example.route.mq;

import com.example.common.constant.Constants;
import com.example.common.proto.Header;
import com.example.common.proto.IM_Message;
import com.example.common.serializer.kyro.KryoSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j(topic = "MQSend")
public class MQSend {
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    MQCallBack callBack;

    @Autowired
    KryoSerializer kryoSerializer;

    public void sendMessage(IM_Message msg, boolean isOffline){
        byte[] bytes = kryoSerializer.serialize(msg);
        String id;
        if(isOffline)
            id = "Off:" + msg.getHeader().getMID();
        else
            id = idGenerator(msg);

        CorrelationData correlationData = new CorrelationData(id);
        String routingKey = routingKeyGet(msg);
        rabbitTemplate.convertAndSend(Constants.RabbitmqConstants.Routing, routingKey, bytes, correlationData);
    }

    public String idGenerator(IM_Message message){
        Header header = message.getHeader();
        long uid = header.getUID();
        long mid = header.getMID();
        long tid = header.getTID();
        byte type = header.getType();
        long gid = header.getGID();
        String id = "";
        switch (type){
            case Constants.CommandType.P2P_MSG:{
                id = "P2P:" + mid + ":" + uid;
                break;
            }
            case Constants.CommandType.GROUP_MSG:{
                id = "Group:" + mid + ":" + uid;
                break;
            }
            case Constants.CommandType.P2P_ACK:{
                id = "P2PAck:" + mid;
                break;
            }
            case Constants.CommandType.GROUP_ACK:{
                id = "GroupAck:" + mid + ":" + tid + ":" + gid;
                break;
            }
            default:
                break;
        }
        return id;
    }

    public String routingKeyGet(IM_Message message){
        byte type = message.getHeader().getType();
        String routingKey = "";
        switch (type){
            case Constants.CommandType.P2P_MSG:{
                routingKey = Constants.RabbitmqConstants.P2PMessage;
                break;
            }
            case Constants.CommandType.GROUP_MSG:{
                routingKey = Constants.RabbitmqConstants.GroupMessage;
                break;
            }
            case Constants.CommandType.P2P_ACK:{
                routingKey = Constants.RabbitmqConstants.P2PACK;
                break;
            }
            case Constants.CommandType.GROUP_ACK:{
                routingKey = Constants.RabbitmqConstants.GroupACK;
                break;
            }
            default:
                break;
        }
        return routingKey;
    }
}
