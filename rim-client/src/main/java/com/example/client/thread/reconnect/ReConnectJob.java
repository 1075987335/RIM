package com.example.client.thread.reconnect;

import com.example.client.client.RIMClient;
import com.example.client.util.SpringBeanFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "ReConnectJob")
public class ReConnectJob implements Runnable {

    RIMClient client;

    ReConnectManager reConnectManager;

    int count;

    int index;

    public ReConnectJob(int count) {
        client = SpringBeanFactory.getBean(RIMClient.class);
        reConnectManager = SpringBeanFactory.getBean(ReConnectManager.class);
        this.count = count;
    }

    @Override
    public void run() {
        index++;
        boolean isSuccess = false;
        if (index <= count) {
            log.info("执行第{}次重连任务...", index);
            isSuccess = client.reConnect();
        } else {
            reConnectManager.closePool();
        }
        if (isSuccess == true) {
            log.info("重连成功！");
        }
    }
}
