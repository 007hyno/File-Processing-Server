package com.hyno.file_processing_server.service;

import com.hyno.file_processing_server.model.Job;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class JobQueue {

    private final BlockingQueue<Job> jobQueue = new LinkedBlockingQueue<>();

    public void addJob(Job job) throws InterruptedException{
        jobQueue.add(job);
    }

    public Job getJob() throws InterruptedException{
        return jobQueue.take();
    }

}