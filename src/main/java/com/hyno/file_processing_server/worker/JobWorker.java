package com.hyno.file_processing_server.worker;

import com.hyno.file_processing_server.service.ImageProcessingService;
import com.hyno.file_processing_server.service.JobQueue;
import com.hyno.file_processing_server.model.JobStatus;
import com.hyno.file_processing_server.model.Job;
import com.hyno.file_processing_server.repository.JobRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class JobWorker {
private final JobQueue jobqueue;
private final JobRepository jobRepository;
private final ImageProcessingService imageProcessingService;

    public JobWorker(JobQueue jobqueue, JobRepository jobRepository, ImageProcessingService imageProcessingService) {
        this.jobqueue = jobqueue;
        this.jobRepository = jobRepository;
        this.imageProcessingService = imageProcessingService;
    }

    public void startWorker() throws InterruptedException {
        while(true){
            System.out.println("🚀 [" + Thread.currentThread().getName() + "] is Ready! ");
            Job job = jobqueue.getJob();
            System.out.println("🚀 [" + Thread.currentThread().getName() + "] picked up job: " + job.getId());
            try{
            job.setStatus(JobStatus.PROCESSING);
            jobRepository.save(job);
            imageProcessingService.process(job);
            System.out.println("🚀 [" + Thread.currentThread().getName() + "] finises job: " + job.getId());
            } catch (Exception e) {
                System.err.println("Error during processing: " + e.getMessage());
                job.setStatus(JobStatus.FAILED);
                jobRepository.save(job);
            }
        }
    }

    @PostConstruct
    public void init(){
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