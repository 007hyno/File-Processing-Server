package com.hyno.file_processing_server.controller;

import com.hyno.file_processing_server.service.JobQueue;
import com.hyno.file_processing_server.model.Job;
import com.hyno.file_processing_server.repository.JobRepository;
import com.hyno.file_processing_server.model.JobRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/job")
public class JobController {
    private final JobRepository jobRepository;
    private final JobQueue jobQueue;

    public JobController(JobRepository jobRepository, JobQueue jobQueue){
        this.jobRepository = jobRepository;
        this.jobQueue = jobQueue;
    }

    @PostMapping
    public Job createJob(@RequestBody JobRequest request) throws InterruptedException {
    Job job = new Job();
    job.setStatus(request.getStatus());
    job.setInputPath(request.getInputPath());
    job.setOutputPath(request.getOutputPath());

    Job savedJob = jobRepository.save(job);
    jobQueue.addJob(savedJob);
    return savedJob;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Job> getJobStatus(@PathVariable Long id){
        return jobRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
