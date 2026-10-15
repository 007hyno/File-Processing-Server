package com.hyno.file_processing_server.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Job {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Enumerated(EnumType.STRING)
        private JobStatus status;

        private String inputPath;
        private String outputPath;
        private String errorMessage;

        @Column(nullable = false, updatable = false)
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        @PrePersist
        public void onCreate() {
                this.createdAt = LocalDateTime.now();
                this.updatedAt = LocalDateTime.now();
        }

        @PreUpdate
        public void onUpdate() {
                this.updatedAt = LocalDateTime.now()    ;
        }

        public LocalDateTime getUpdatedAt() {
                return updatedAt;
        }

        public Long getId() {
                return id;
        }

        public JobStatus getStatus() {
                return status;
        }

        public String getOutputPath() {
                return outputPath;
        }

        public String getInputPath() {
                return inputPath;
        }

        public void setStatus(JobStatus status) {
                this.status = status;
        }

        public void setOutputPath(String outputPath) {
                this.outputPath = outputPath;
        }

        public void setInputPath(String inputPath) {
                this.inputPath = inputPath;
        }
        public LocalDateTime getCreatedAt() {
                return createdAt;
        }
}