package com.hyno.file_processing_server.worker;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class WorkerPoolConfig{
    @Bean(name = "jobWorkerExecutor")
    public ExecutorService jobWorkerExecutor() {
        // Query CPU cores available to the JVM
        int cores = Runtime.getRuntime().availableProcessors();

        // For CPU-heavy tasks: pool size roughly matches core count
//        int corePoolSize = Math.max(2, cores);
        int corePoolSize = 2; // to simulate low thread count
        int maxPoolSize = corePoolSize; // Fixed pool to avoid context switching
        int queueCapacity = 2;         // Bounded queue holding waiting jobs (using 2 as max limit to simulate caller run policy())

        AtomicInteger threadCount = new AtomicInteger(1);

        return new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                60L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(queueCapacity),
                runnable -> new Thread(runnable, "worker-pool-" + threadCount.getAndIncrement()),
                new ThreadPoolExecutor.CallerRunsPolicy() // Rejection strategy when full
        );
    }
}
