package com.example.common.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {

    private final String prefix;

    private boolean isDeamon;


    private final AtomicInteger threadNumber = new AtomicInteger(0);

    public NamedThreadFactory(String prefix, boolean isDeamon) {
        this.prefix = prefix;
        this.isDeamon = isDeamon;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(null, r, prefix + threadNumber.incrementAndGet());
        if (!thread.isDaemon() && isDeamon) {
            thread.setDaemon(true);
        }
        return thread;
    }
}
