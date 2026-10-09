package com.hyno.file_processing_server.model;

public class JobRequest {
    private JobStatus status;
    private String outputPath;
    private String inputPath;

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public void setInputPath(String inputPath) {
        this.inputPath = inputPath;
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
}
