package com.hyno.file_processing_server.model;

import jakarta.persistence.*;

@Entity
public class Job {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Enumerated(EnumType.STRING)
        private JobStatus status;

        private String outputPath;
        private String inputPath;

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
}