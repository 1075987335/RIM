package com.example;

import com.example.client.request.SendMessageVo;
import com.example.client.response.AckVo;

public interface MessageSendService {

    /**
     * 使用到type的发送消息Vo
     * @param sendMessageVo
     */
    void sendMessageToServer(SendMessageVo sendMessageVo);

    /**
     * 向客户端返回ack
     * @param ackVo
     */
    void sendAckToClient(AckVo ackVo);

    /**
     * 向存储服务发送ack
     * @param ackVo
     */
    void sendAckToStoreService(AckVo ackVo);

    /**
     * 重发消息给服务器
     * @param sendMessageVo
     */
    void sendRetryMessageToServer(SendMessageVo sendMessageVo);

    /**
     * 发送离线消息
     * @param sendMessageVo
     */
    void sendOfflineMessage(SendMessageVo sendMessageVo);

    /**
     * 保存离线消息
     * @param sendMessageVo
     */
    void storeOfflineMessage(SendMessageVo sendMessageVo);

}
