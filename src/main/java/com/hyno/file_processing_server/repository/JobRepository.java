package com.hyno.file_processing_server.repository;

import com.hyno.file_processing_server.model.Job;
import com.hyno.file_processing_server.model.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByStatus(JobStatus status);
}
