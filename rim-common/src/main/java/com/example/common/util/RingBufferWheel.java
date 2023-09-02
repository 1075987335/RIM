package com.example.common.util;


import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j(topic = "RingBufferWheel")
public class RingBufferWheel {
    /**
     * default ring buffer size
     */
    private static final int STATIC_RING_SIZE = 64;

    private Object[] ringBuffer;

    private int bufferSize;

    /**
     * business thread pool
     */
    private ThreadPoolExecutor executorService;

    private volatile int size = 0;

    /***
     * task stop sign
     */
    private volatile boolean stop = false;

    /**
     * task start sign
     */
    private volatile AtomicBoolean start = new AtomicBoolean(false);

    /**
     * total tick times
     */
    private AtomicInteger tick = new AtomicInteger();

    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    private AtomicInteger taskId = new AtomicInteger();
    private Map<Long, Task> taskMap = new ConcurrentHashMap<>(16);

    public RingBufferWheel(ThreadPoolExecutor executorService) {
        this.executorService = executorService;
        this.bufferSize = STATIC_RING_SIZE;
        this.ringBuffer = new Object[bufferSize];
    }

    /**
     * Create a new delay task ring buffer by custom buffer size
     *
     * @param executorService the business thread pool
     * @param bufferSize      custom buffer size
     */
    public RingBufferWheel(ThreadPoolExecutor executorService, int bufferSize) {
        this(executorService);

        if (!powerOf2(bufferSize)) {
            throw new RuntimeException("bufferSize=[" + bufferSize + "] must be a power of 2");
        }
        this.bufferSize = bufferSize;
        this.ringBuffer = new Object[bufferSize];
    }

    /**
     * Add a task into the ring buffer(thread safe)
     *
     * @param task business task extends {@link Task}
     */
    public long addTask(Task task) {
        int key = task.getDelay_time();
        try {
            lock.lock();
            int index = mod(key, bufferSize);
            task.setIndex(index);
            Set<Task> tasks = get(index);

            int cycleNum = cycleNum(key, bufferSize);
            if (tasks != null) {
                task.setCycleNum(cycleNum);
                tasks.add(task);
            } else {
                task.setIndex(index);
                task.setCycleNum(cycleNum);
                Set<Task> sets = new HashSet<>();
                sets.add(task);
                put(key, sets);
            }
            //id = taskId.incrementAndGet();
            //task.setTaskId(id);
            taskMap.put(task.getTaskId(), task);
            size++;
        } finally {
            lock.unlock();
        }
        start();
        return task.getTaskId();
    }


    /**
     * Cancel task by taskId
     *
     * @param id unique id through {@link #addTask(Task)}
     * @return
     */
    public boolean cancel(long id) {

        boolean flag = false;
        Set<Task> tempTask = new HashSet<>();

        try {
            lock.lock();
            Task task = taskMap.get(id);
            if (task == null) {
                return false;
            }

            Set<Task> tasks = get(task.getIndex());
            for (Task tk : tasks) {
                if (tk.getDelay_time() == task.getDelay_time() && tk.getCycleNum() == task.getCycleNum()) {
                    size--;
                    flag = true;
                    taskMap.remove(id);
                } else {
                    tempTask.add(tk);
                }

            }
            //update origin data
            ringBuffer[task.getIndex()] = tempTask;
        } finally {
            lock.unlock();
        }
        log.info("消息确认成功，超时计时器取消...");

        return flag;
    }

    /**
     * Thread safe
     *
     * @return the size of ring buffer
     */
    public int taskSize() {
        return size;
    }

    /**
     * Same with method {@link #taskSize}
     *
     * @return
     */
    public int taskMapSize() {
        return taskMap.size();
    }

    /**
     * Start background thread to consumer wheel timer, it will always run until you call method {@link #stop}
     */
    public void start() {
        if (!start.get()) {

            if (start.compareAndSet(start.get(), true)) {
                log.info("Delay task is starting");
                Thread job = new Thread(new TriggerJob());
                job.setName("consumer RingBuffer thread");
                job.start();
                start.set(true);
            }

        }
    }

    /**
     * Stop consumer ring buffer thread
     *
     * @param force True will force close consumer thread and discard all pending tasks
     *              otherwise the consumer thread waits for all tasks to completes before closing.
     */
    public void stop(boolean force) {
        if (force) {
            log.info("Delay task is forced stop");
            stop = true;
            executorService.shutdownNow();
        } else {
            log.info("Delay task is stopping");
            if (taskSize() > 0) {
                try {
                    lock.lock();
                    condition.await();
                    stop = true;
                } catch (InterruptedException e) {
                    log.error("InterruptedException", e);
                } finally {
                    lock.unlock();
                }
            }
            executorService.shutdown();
        }
    }


    private Set<Task> get(int index) {
        return (Set<Task>) ringBuffer[index];
    }

    private void put(int key, Set<Task> tasks) {
        int index = mod(key, bufferSize);
        ringBuffer[index] = tasks;
    }

    /**
     * Remove and get task list.
     *
     * @param key
     * @return task list
     */
    private Set<Task> remove(int key) {
        Set<Task> tempTask = new HashSet<>();
        Set<Task> result = new HashSet<>();

        Set<Task> tasks = (Set<Task>) ringBuffer[key];
        if (tasks == null) {
            return result;
        }

        for (Task task : tasks) {
            if (task.getCycleNum() == 0) {
                result.add(task);

                size2Notify();
            } else {
                // decrement 1 cycle number and update origin data
                task.setCycleNum(task.getCycleNum() - 1);
                tempTask.add(task);
            }
            // remove task, and free the memory.
            taskMap.remove(task.getTaskId());
        }

        //update origin data
        ringBuffer[key] = tempTask;

        return result;
    }

    private void size2Notify() {
        try {
            lock.lock();
            size--;
            if (size == 0) {
                condition.signal();
            }
        } finally {
            lock.unlock();
        }
    }

    private boolean powerOf2(int target) {
        if (target < 0) {
            return false;
        }
        int value = target & (target - 1);
        if (value != 0) {
            return false;
        }

        return true;
    }

    private int mod(int target, int mod) {
        // equals target % mod
        target = target + tick.get();
        return target & (mod - 1);
    }

    private int cycleNum(int target, int mod) {
        //equals target/mod
        return target >> Integer.bitCount(mod - 1);
    }

    /**
     * An abstract class used to implement business.
     */
    public abstract static class Task extends Thread {

        private int index;

        private int cycleNum;

        protected int delay_time;

        /**
         * The unique ID of the task
         */
        private long taskId;

        @Override
        public void run() {
        }

        public int getDelay_time() {
            return delay_time;
        }

        /**
         * @param delay_time Delay time(seconds)
         */
        public void setDelay_time(int delay_time) {
            this.delay_time = delay_time;
        }

        public int getCycleNum() {
            return cycleNum;
        }

        private void setCycleNum(int cycleNum) {
            this.cycleNum = cycleNum;
        }

        public int getIndex() {
            return index;
        }

        private void setIndex(int index) {
            this.index = index;
        }

        public long getTaskId() {
            return taskId;
        }

        public void setTaskId(long taskId) {
            this.taskId = taskId;
        }
    }

    private class TriggerJob implements Runnable {
        @Override
        public void run() {
            int index = 0;
            while (!stop) {
                try {
                    Set<Task> tasks = remove(index);
                    for (Task task : tasks) {
                        executorService.submit(task);
                    }
                    if (++index > bufferSize - 1) {
                        index = 0;
                    }
                    //Total tick number of records
                    tick.incrementAndGet();
                    TimeUnit.SECONDS.sleep(1);
                } catch (Exception e) {
                    log.error("Exception", e);
                }
            }
            log.info("Delay task has stopped");
        }
    }
}
