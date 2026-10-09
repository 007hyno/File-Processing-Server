package com.hyno.file_processing_server.controller;

import com.hyno.file_processing_server.model.Job;
import com.hyno.file_processing_server.model.JobStatus;
import com.hyno.file_processing_server.model.UserRequest;
import com.hyno.file_processing_server.service.ImageJobService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.function.EntityResponse;
import org.springframework.web.servlet.function.ServerResponse;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api")
public class Controller {
    private final ImageJobService imageJobService;

    public Controller(ImageJobService imageJobService) {
        this.imageJobService = imageJobService;
    }

    @GetMapping("/")
    public String first(){
        return "Api is alive :)";
    }

    @PostMapping("/post")
        public ResponseEntity<UserRequest> post(@RequestBody UserRequest userRequest){
        return new ResponseEntity<UserRequest>(userRequest, HttpStatus.CREATED);
        }

    @PostMapping("/upload-image")
    public ResponseEntity<?> updateImage(@RequestParam("image") MultipartFile file, @RequestParam("text") String text){
        try {
        Job job = imageJobService.processImage(file,text);
        return ResponseEntity.ok()
                .body(job);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @GetMapping("/image/{id}")
    public ResponseEntity<?> getImageProcessingStatus(@PathVariable("id") Long id){
        Job job = imageJobService.getJob(id);
        if(job == null){
            System.out.println("job is null");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Job not found with id: " + id);
        }
        return ResponseEntity.status(HttpStatus.FOUND).body(job);
    }

    @GetMapping("/image/download/{id}")
    public ResponseEntity<?> getProcessedImage(@PathVariable("id") Long id){
        try{
            Job job = imageJobService.getJob(id);
            System.out.println("got the job..");

            if(job == null){
                System.out.println("job is null");
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Job not found with id: " + id);
            }

            JobStatus jobStatus = job.getStatus();
            if (job.getStatus() == JobStatus.FAILED) {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body("Image processing failed.");
            }

            if(jobStatus != JobStatus.COMPLETED){
                return ResponseEntity.status(HttpStatus.ACCEPTED)
                        .body("File is processing..");
            }
            String outputDirPath = job.getOutputPath();
            Path outputPath = Paths.get(outputDirPath);
            Resource resource = new FileSystemResource(outputPath);
            String fileContentType = Files.probeContentType(Paths.get(outputDirPath));

            if (fileContentType == null) {
                fileContentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(fileContentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + outputPath.getFileName().toString() + "\"")
                    .body(resource);

        }catch (IOException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File upload failed: "+e.getMessage());
        }
    }
}

