package com.hyno.file_processing_server.repository;

import com.hyno.file_processing_server.model.Job;
import com.hyno.file_processing_server.model.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByStatus(JobStatus status);

    // Translates to: SELECT ... FROM jobs WHERE status = 'QUEUED' ORDER BY created_at ASC FOR UPDATE
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT j FROM Job j WHERE j.status = :status ORDER BY j.createdAt ASC")
    List<Job> findByStatusForUpdate(@Param("status") JobStatus status);
}
