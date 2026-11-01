package com.hyno.file_processing_server.service;

import com.hyno.file_processing_server.model.Job;
import com.hyno.file_processing_server.model.JobStatus;
import com.hyno.file_processing_server.repository.JobRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JobClaimService {

    private final JobRepository jobRepository;

    public JobClaimService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Transactional
    public Job claimNextJob() {
        // 1. SELECT ... FOR UPDATE locks the row
        List<Job> queuedJobs = jobRepository.findByStatusForUpdate(JobStatus.QUEUED);

        if (queuedJobs.isEmpty()) {
            return null;
        }

        Job job = queuedJobs.get(0);

        // 2. Mark as PROCESSING while holding the lock
        job.setStatus(JobStatus.PROCESSING);
        Job savedJob = jobRepository.save(job);

        // 3. When this method returns, the transaction COMMITS and the lock is RELEASED
        return savedJob;
    }
}