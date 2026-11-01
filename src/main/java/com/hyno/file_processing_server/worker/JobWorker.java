package com.hyno.file_processing_server.worker;

import com.hyno.file_processing_server.service.ImageProcessingService;
import com.hyno.file_processing_server.service.JobClaimService;
import com.hyno.file_processing_server.service.JobQueue;
import com.hyno.file_processing_server.model.JobStatus;
import com.hyno.file_processing_server.model.Job;
import com.hyno.file_processing_server.repository.JobRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JobWorker {
private final JobQueue jobqueue;
private final JobRepository jobRepository;
private final ImageProcessingService imageProcessingService;
private final JobClaimService jobClaimService;

    public JobWorker(JobQueue jobqueue, JobRepository jobRepository, ImageProcessingService imageProcessingService, JobClaimService jobClaimService) {
        this.jobqueue = jobqueue;
        this.jobRepository = jobRepository;
        this.imageProcessingService = imageProcessingService;
        this.jobClaimService = jobClaimService;
    }

    // Inside your worker loop:
    public void startWorker() throws InterruptedException {
        while (true) {
            // Atomically claim next job in a short transaction
            Job job = jobClaimService.claimNextJob();

            if (job == null) {
                Thread.sleep(500); // Back off if nothing in queue
                continue;
            }

            try {
                System.out.println("🚀 [" + Thread.currentThread().getName() + "] STARTED Job ID: " + job.getId());
                imageProcessingService.process(job);

                job.setStatus(JobStatus.COMPLETED);
                jobRepository.save(job);
                System.out.println("✅ [" + Thread.currentThread().getName() + "] FINISHED Job ID: " + job.getId());
            } catch (Exception e) {
                System.err.println("❌ [" + Thread.currentThread().getName() + "] FAILED Job ID: " + job.getId());
                job.setStatus(JobStatus.FAILED);
                job.setErrorMessage(e.getMessage());
                jobRepository.save(job);
            }
        }
    }

//    public void startWorker() throws InterruptedException {
//        while (true) {
//            Job job = claimNextJobNaive();
//
//            if (job == null) {
//                // Nothing to do right now; sleep briefly before polling again
//                Thread.sleep(500);
//                continue;
//            }
//
//            try {
//                System.out.println("🚀 [" + Thread.currentThread().getName() + "] STARTED Job ID: " + job.getId());
//                imageProcessingService.process(job);
//                job.setStatus(JobStatus.COMPLETED);
//                jobRepository.save(job);
//                System.out.println("✅ [" + Thread.currentThread().getName() + "] FINISHED Job ID: " + job.getId());
//            } catch (Exception e) {
//                System.err.println("❌ [" + Thread.currentThread().getName() + "] FAILED Job ID: " + job.getId());
//                job.setStatus(JobStatus.FAILED);
//                job.setErrorMessage(e.getMessage());
//                jobRepository.save(job);
//            }
//        }
//    }
//
//    // Testing non ATOMIC scenario
//    public Job claimNextJobNaive() {
//        // 1. SELECT: Find the oldest queued job
//        List<Job> queuedJobs = jobRepository.findByStatus(JobStatus.QUEUED);
//        if (queuedJobs.isEmpty()) {
//            return null;
//        }
//
//        Job job = queuedJobs.get(0);
//
//        // 2. DELAY: Artificially amplify the race condition window by 50ms
//        try {
//            Thread.sleep(50);
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }
//
//        // 3. UPDATE: Change status and persist
//        job.setStatus(JobStatus.PROCESSING);
//        return jobRepository.save(job);
//    }

    @PostConstruct
    public void init() throws InterruptedException {
        List<Job> queuedJobs = jobRepository.findByStatus(JobStatus.QUEUED);
        List<Job> interruptedJobs = jobRepository.findByStatus(JobStatus.PROCESSING);

        // Reset interrupted jobs so state transitions remain clean
        for (Job job : interruptedJobs) {
            job.setStatus(JobStatus.QUEUED);
            jobRepository.save(job);
        }

        for (Job j : queuedJobs) {
            jobqueue.addJob(j);
        }
        for (Job j : interruptedJobs) {
            jobqueue.addJob(j);
        }
        runWorker("worker-1");
        runWorker("worker-2");
    }

    public void runWorker(String wrokerName){
        Thread workerThread = new Thread(() -> {
            try {
                startWorker();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        },wrokerName);

        workerThread.start();
    }
}