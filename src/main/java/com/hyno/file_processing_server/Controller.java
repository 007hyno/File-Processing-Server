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

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
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
            System.out.println("Api request received.");

            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String filename = file.getOriginalFilename();
            System.out.println("Original file name: "+filename);

            int canvasWidth = 300;
            int canvasHeight = 300;
            int offSet = 20;

            BufferedImage originalImage = ImageIO.read(file.getInputStream());

            BufferedImage newImage = new BufferedImage(canvasWidth, canvasHeight + offSet, BufferedImage.TYPE_INT_RGB);

            System.out.println("Create new image canvas");

            Graphics2D graphics2D = newImage.createGraphics();

            graphics2D.setColor(Color.WHITE);
            graphics2D.fillRect(0, 0, canvasWidth, canvasHeight + offSet);

            Font myFont = new Font("Arial", Font.BOLD, 26);
            graphics2D.setFont(myFont);
            graphics2D.drawImage(originalImage, 0, 0, canvasWidth, canvasHeight, null);
            graphics2D.setColor(Color.BLACK);
            graphics2D.drawString("neti-neti",0,canvasHeight + offSet);

            graphics2D.dispose();

            System.out.println("Image processing completed");

            Path filePath = uploadPath.resolve("resized_" + filename);

            ImageIO.write(newImage, "jpg", filePath.toFile());

//            file.transferTo(filePath);

            Resource resource = new FileSystemResource(filePath);

            String fileContentType = Files.probeContentType(filePath);

            if (fileContentType == null) {
                fileContentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }

            System.out.println("Api request successful");
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

