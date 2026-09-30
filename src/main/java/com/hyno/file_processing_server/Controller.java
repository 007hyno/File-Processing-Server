package com.hyno.file_processing_server;

import org.apache.catalina.User;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api")
public class Controller {

    @GetMapping("/")
    public String first(){
        return "Api is alive :)";
    }

    @PostMapping("/post")
        public ResponseEntity<UserRequest> post(@RequestBody UserRequest userRequest){
        return new ResponseEntity<UserRequest>(userRequest, HttpStatus.CREATED);
        }
    @PostMapping("/upload-image")
    public ResponseEntity<?> updateImage(@RequestParam("image") MultipartFile file){
        String uploadDir = "imageUpload";
        try {

            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String filename = file.getOriginalFilename();

            Path filePath = uploadPath.resolve(filename);

            file.transferTo(filePath);

            Resource resource = new FileSystemResource(filePath);

            String fileContentType = Files.probeContentType(filePath);

            if (fileContentType == null) {
                fileContentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(fileContentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);

        }catch (IOException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File upload failed: "+e.getMessage());
        }

    }
}

