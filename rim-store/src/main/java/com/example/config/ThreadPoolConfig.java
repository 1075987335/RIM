package com.example.config;

import com.example.common.util.NamedThreadFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadPoolConfig {

    @Bean("storeJobExecutor")
    public ThreadPoolExecutor storeJobExecutor() {
        NamedThreadFactory namedThreadFactory = new NamedThreadFactory("StoreJobExecutor--", false);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                25,
                50,
                15,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(100),
                namedThreadFactory,
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        return executor;
    }
}
