package com.hyno.file_processing_server.repository;

import com.hyno.file_processing_server.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {

}
