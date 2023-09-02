package com.example.route.service;

import com.example.MessageSendService;
import com.example.OfflineMessageService;
import com.example.client.request.SendMessageVo;
import com.example.route.utils.ConvertDubboPOJO;
import com.example.route.utils.ConvertToSendMessageVo;
import com.example.route.utils.DubboUtil;
import com.example.storeapi.OfflineMessageSend;
import com.example.storeapi.vo.OfflineRequestVo;
import com.example.storeapi.vo.OfflineResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

@Service
@DubboService
@Slf4j
public class OfflineMessageServiceImpl implements OfflineMessageService {

    @Autowired
    BeforeSendService beforeSendService;

    @Autowired
    MessageSendService messageSendService;

    @Autowired
    ThreadPoolExecutor sendJobExecutor;

    @Override
    public void sendOfflineMessage(OfflineRequestVo offlineReqVO) {

        Object[] parameters = new Object[]{offlineReqVO};
        List result = (List) DubboUtil.getDubboService(OfflineMessageSend.class, "localhost", 1503, "getOfflineMessage", parameters);
        if (result.size() > 0) {
            //转换成需求的类型
            List<OfflineResponseVo> offlineMessage = ConvertDubboPOJO.parseToOfflineResponseVo(result);
            List<SendMessageVo> sendMessageVoList = new ArrayList<>();
            for (OfflineResponseVo vo : offlineMessage) {
                sendMessageVoList.add(ConvertToSendMessageVo.parse(vo));
            }
            log.info("即将发送离线消息：{}", sendMessageVoList);
            for (SendMessageVo v : sendMessageVoList) {
                messageSendService.sendOfflineMessage(v);
            }
        }
    }
}
