package com.example.client.config;

import com.example.common.util.NamedThreadFactory;
import com.example.common.util.RingBufferWheel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadPoolConfig {
    @Bean("singleJobExecutor")
    public ThreadPoolExecutor singleJobExecutor() {
        NamedThreadFactory factory = new NamedThreadFactory("ScanJobExecutor--", true);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                1,
                1,
                0,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(1),
                factory,
                new ThreadPoolExecutor.DiscardPolicy());
        return executor;
    }

    @Bean("outTimeJobExecutor")
    public RingBufferWheel outTimeJobExecutor() {
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

    @Bean("scheduleJobExecutor")
    public ScheduledThreadPoolExecutor scheduleJobExecutor() {
        NamedThreadFactory factory = new NamedThreadFactory("SchedulJobExecutor--", true);
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1, factory);
        return executor;
    }
}
