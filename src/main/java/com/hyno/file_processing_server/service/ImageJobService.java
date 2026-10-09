package com.hyno.file_processing_server.service;

import com.hyno.file_processing_server.model.Job;
import com.hyno.file_processing_server.model.JobStatus;
import com.hyno.file_processing_server.repository.JobRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

@Service
public class ImageJobService {
    private final JobRepository jobRepository;
    private final JobQueue jobQueue;

    public ImageJobService(JobRepository jobRepository, JobQueue jobQueue) {
        this.jobRepository = jobRepository;
        this.jobQueue = jobQueue;
    }

    public Job processImage(MultipartFile file, String text){
        Job job = new Job();
        try {
            BufferedImage inputImage = ImageIO.read(file.getInputStream());
            Path inputDirPath = Paths.get("inputDirName");

            if (!Files.exists(inputDirPath)) {
                Files.createDirectories(inputDirPath);
            }
            long timestamp = Instant.now().toEpochMilli();
            String inputImageName = file.getOriginalFilename();
            Path fullInputDirPath = inputDirPath.resolve(timestamp+inputImageName);
            ImageIO.write(inputImage, "jpg", fullInputDirPath.toFile());

            job.setStatus(JobStatus.QUEUED);
            job.setInputPath(fullInputDirPath.toString());

            Job savedJob = jobRepository.save(job);
            jobQueue.addJob(savedJob);
            return savedJob;
        } catch (IOException e){
            job.setStatus(JobStatus.FAILED);
            jobRepository.save(job);
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public Job getJob(Long id){
        return jobRepository.findById(id).orElse(null);
    }
}
