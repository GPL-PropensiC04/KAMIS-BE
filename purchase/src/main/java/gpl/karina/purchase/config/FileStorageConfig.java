package gpl.karina.purchase.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class FileStorageConfig {

    @Value("${file.upload-dir:purchase-images}")
    private String uploadDir;

    /**
     * Creates and returns the upload directory path
     * If the directory doesn't exist, it will be created
     */
    @Bean
    public Path fileStorageLocation() {
        Path fileStorageLocation = Paths.get(uploadDir)
                .toAbsolutePath().normalize();

        // Create directory if it doesn't exist
        File directory = fileStorageLocation.toFile();
        if (!directory.exists()) {
            directory.mkdirs();
        }

        return fileStorageLocation;
    }
}