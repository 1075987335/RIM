package com.example.route.config;

import com.example.common.util.NamedThreadFactory;
import com.example.common.util.RingBufferWheel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadPoolConfig {

    @Bean("sendJobExecutor")
    public ThreadPoolExecutor sendJobExecutor(){
        NamedThreadFactory factory = new NamedThreadFactory("SendJobExecutor--", false);
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                25,
                50,
                15,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(),
                factory,
                new ThreadPoolExecutor.CallerRunsPolicy());
        return threadPoolExecutor;
    }

    @Bean("outTimeJobExecutor")
    public RingBufferWheel outTimeJobExecutor(){
        NamedThreadFactory factory = new NamedThreadFactory("DelayJobExecutor--", false);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                25,
                50,
                15,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(100),
                factory,
                new ThreadPoolExecutor.CallerRunsPolicy());
        RingBufferWheel ringBufferWheel = new RingBufferWheel(executor);
        return ringBufferWheel;
    }
}
