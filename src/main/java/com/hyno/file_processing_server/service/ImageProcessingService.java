package com.hyno.file_processing_server.service;

import com.hyno.file_processing_server.model.Job;
import com.hyno.file_processing_server.model.JobStatus;
import com.hyno.file_processing_server.repository.JobRepository;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class ImageProcessingService {
    private final JobRepository jobRepository;

    public ImageProcessingService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public void process(Job job){
        try {
        System.out.println("🪀 Image Processor got the job.."+job.getId());
        Long id = job.getId();
        String inputDirName = "inputDirName";
        String outputDirName = "outputDirName";
        String storedInputPath = job.getInputPath();

        System.out.println("storedInputPath: "+storedInputPath);

        Path inputDirPath = Paths.get(job.getInputPath());
        File inputFile = inputDirPath.toFile();
        String nameWithoutExtension = inputFile.getName().replaceFirst("\\.[^.]+$", "");
        System.out.println("🪀 name of the stored file is "+ nameWithoutExtension);
//        Path outputDirPath = Paths.get(outputDirName,nameWithoutExtension);
        Path outputDirPath = Paths.get(outputDirName,inputFile.getName());

        BufferedImage originalImage = ImageIO.read(inputFile);
        System.out.println("🪀 Read the original image");
//        String watermarkText = (!text.isEmpty()) ? text : "neti-neti";

            if (!Files.exists(outputDirPath)) {
                Files.createDirectories(outputDirPath);
            }

            int canvasWidth = 300;
            int canvasHeight = 300;
            int offSet = 20;

            //adding timer for testing
            try{
                Thread.sleep(4000);
            }catch (Exception e){
                throw new RuntimeException();
            }

            BufferedImage newImage = new BufferedImage(canvasWidth, canvasHeight + offSet, BufferedImage.TYPE_INT_RGB);
        System.out.println("🪀 Xreated a new empty image");

            Graphics2D graphics2D = newImage.createGraphics();
            graphics2D.setColor(Color.WHITE);
            graphics2D.fillRect(0, 0, canvasWidth, canvasHeight + offSet);
            Font myFont = new Font("Arial", Font.BOLD, 26);
            graphics2D.setFont(myFont);
            graphics2D.drawImage(originalImage, 0, 0, canvasWidth, canvasHeight, null);
            graphics2D.setColor(Color.BLACK);
//            graphics2D.drawString(watermarkText,0,canvasHeight + offSet);
            graphics2D.dispose();
            System.out.println("🪀 new image created ");
            ImageIO.write(newImage, "jpg", outputDirPath.toFile());
            System.out.println("🪀 New image saving");

            job.setStatus(JobStatus.COMPLETED);
            job.setOutputPath(outputDirPath.toString());
            jobRepository.save(job);
            System.out.println("🪀 Updated the job");

        }catch (IOException e) {
            System.out.println("🪀 Error catch");
            throw new RuntimeException(e);
        }
    }
}
