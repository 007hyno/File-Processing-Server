package com.hyno.file_processing_server.worker;

import com.hyno.file_processing_server.model.Job;
import com.hyno.file_processing_server.model.JobStatus;
import com.hyno.file_processing_server.repository.JobRepository;
import com.hyno.file_processing_server.service.ImageProcessingService;
import com.hyno.file_processing_server.service.JobClaimService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class WorkerManager {

    private final JobClaimService jobClaimService;
    private final JobRepository jobRepository;
    private final ImageProcessingService imageProcessingService;
    private final ExecutorService executorService;

    public WorkerManager(JobClaimService jobClaimService,
                         JobRepository jobRepository,
                         ImageProcessingService imageProcessingService,
                         @Qualifier("jobWorkerExecutor") ExecutorService executorService) {
        this.jobClaimService = jobClaimService;
        this.jobRepository = jobRepository;
        this.imageProcessingService = imageProcessingService;
        this.executorService = executorService;
    }

    @PostConstruct
    public void startSchedulerLoop() {
        // Background dispatcher submitting claimed jobs to the pool
        Thread dispatcherThread = new Thread(this::pollAndDispatch, "dispatcher-thread");
        dispatcherThread.setDaemon(true);
        dispatcherThread.start();
    }

    private void pollAndDispatch() {
        while (!Thread.currentThread().isInterrupted()) {
            Job job = jobClaimService.claimNextJob();

            if (job == null) {
                try {
                    Thread.sleep(500); // Polling backoff
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
                continue;
            }

            // Submit task to the managed worker pool
            executorService.submit(() -> processJob(job));
        }
    }

    private void processJob(Job job) {
        try {
            System.out.println("🚀 [" + Thread.currentThread().getName() + "] RUNNING Job ID: " + job.getId());
            imageProcessingService.process(job);

            job.setStatus(JobStatus.COMPLETED);
            jobRepository.save(job);
            System.out.println("✅ [" + Thread.currentThread().getName() + "] COMPLETED Job ID: " + job.getId());
        } catch (Exception e) {
            System.err.println("❌ [" + Thread.currentThread().getName() + "] FAILED Job ID: " + job.getId());
            job.setStatus(JobStatus.FAILED);
            job.setErrorMessage(e.getMessage());
            jobRepository.save(job);
        }
    }

    @PreDestroy
    public void stop() {
        System.out.println("🛑 Shutting down WorkerManager...");

        // 1. Disable the executor from accepting any new tasks
        executorService.shutdown();

        try {
            // 2. Wait up to 30 seconds for existing tasks to finish processing
            if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                System.err.println("⚠️ Worker pool did not terminate cleanly within 30s. Forcing shutdown...");
                executorService.shutdownNow();
            } else {
                System.out.println("✅ All in-flight worker tasks completed cleanly.");
            }
        } catch (InterruptedException e) {
            System.err.println("⚠️ Shutdown interrupted, forcing worker termination...");
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}