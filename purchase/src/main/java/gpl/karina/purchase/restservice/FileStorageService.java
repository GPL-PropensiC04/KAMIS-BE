package gpl.karina.purchase.restservice;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String storeFile(MultipartFile file, String filename);

    Resource loadFileAsResource(String fileName);

    void deleteFile(String fileName);
}