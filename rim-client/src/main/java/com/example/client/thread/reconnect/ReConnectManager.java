package com.example.client.thread.reconnect;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

@Component
public class ReConnectManager {

    private ScheduledThreadPoolExecutor scheduleJobExecutor;

    //重连次数
    int count;

    /**
     * 执行重连逻辑
     */
    public void reConnect(int count){
        build();
        scheduleJobExecutor.scheduleAtFixedRate(new ReConnectJob(count),0,3, TimeUnit.SECONDS);
        this.count = count;
    }

    /**
     * 连接成功了就关闭线程池
     */
    public void closePool(){
        scheduleJobExecutor.shutdownNow();
    }

    /**
     * 第一次来的任务要进行初始化操作，只需要一个线程即可
     */
    private void build(){
        if(scheduleJobExecutor == null || scheduleJobExecutor.isShutdown()){
            ThreadFactory factory = new ThreadFactoryBuilder()
                    .setNameFormat("reConnect-job-%d")
                    .setDaemon(true)
                    .build();
            scheduleJobExecutor = new ScheduledThreadPoolExecutor(1, factory);
        }
    }
}
