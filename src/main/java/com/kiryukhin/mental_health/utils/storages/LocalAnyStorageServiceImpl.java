package com.kiryukhin.mental_health.utils.storages;

import com.kiryukhin.mental_health.configs.ApiConfig;
import com.kiryukhin.mental_health.configs.LocalStorageConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocalAnyStorageServiceImpl implements LocalAnyStorageService {
    private final LocalStorageConfig localStorageConfig;
    private final ApiConfig apiConfig;

    @Override
    public String uploadFile(MultipartFile file, String filePath) {
        try {
            Path fullPath = Paths.get(localStorageConfig.getLocalStoragePath()).resolve(filePath).normalize();
            if (!fullPath.startsWith(Paths.get(localStorageConfig.getLocalStoragePath()))) {
                throw new RuntimeException("Invalid path: " + filePath);
            }

            if (!Files.exists(fullPath)) {
                Files.createDirectories(fullPath);
                log.info("Directory created: {}", fullPath);
            }

            String fileName = UUID.randomUUID() + "_" + Objects.requireNonNull(file.getOriginalFilename()).replace(" ", "_");

            Path filePathObj = fullPath.resolve(fileName);

            file.transferTo(filePathObj.toFile());

            log.info("File uploaded successfully: {}", filePathObj.toString());
            return filePath + "/" + fileName;

        } catch (IOException e) {
            log.error("Error saving file locally", e);
            throw new RuntimeException("Error saving file locally", e);
        }
    }

    @Override
    public String updateFile(MultipartFile file, String key) {
        try {
            Path fullPath = Paths.get(localStorageConfig.getLocalStoragePath()).resolve(key).normalize();
            if (!fullPath.startsWith(Paths.get(localStorageConfig.getLocalStoragePath()))) {
                throw new RuntimeException("Invalid path: " + key);
            }

            if (!Files.exists(fullPath)) {
                log.error("File not found for update: {}", key);
                throw new RuntimeException("File not found: " + key);
            }

            file.transferTo(fullPath);

            log.info("File updated successfully: {}", fullPath.toString());
            return key;

        } catch (IOException e) {
            log.error("Error updating file locally", e);
            throw new RuntimeException("Error saving file locally", e);
        }
    }

    @Override
    public String getFileUrl(String key) {
        return apiConfig.getApiBaseUrl() + localStorageConfig.getFilesEndpoint() + "?fileKey=" + key;
    }

    @Override
    public void deleteFile(String key) {
        try {
            Path fullPath = Paths.get(localStorageConfig.getLocalStoragePath()).resolve(key).normalize();
            if (!fullPath.startsWith(Paths.get(localStorageConfig.getLocalStoragePath()))) {
                throw new RuntimeException("Invalid path: " + key);
            }

            if (!Files.exists(fullPath)) {
                log.error("File not found for deletion: {}", key);
                throw new RuntimeException("File not found: " + key);
            }

            Files.delete(fullPath);

            log.info("File deleted successfully: {}", key);

        } catch (IOException e) {
            log.error("Error deleting file: {}", key, e);
            throw new RuntimeException("Error deleting file: " + key, e);
        }
    }

    @Override
    public Resource downloadFile(String filePath) {
        try {
            Path fullPath = Paths.get(localStorageConfig.getLocalStoragePath()).resolve(filePath).normalize();
            if (!fullPath.startsWith(Paths.get(localStorageConfig.getLocalStoragePath()))) {
                throw new RuntimeException("Invalid path: " + filePath);
            }

            if (!Files.exists(fullPath)) {
                log.error("File not found for download: {}", filePath);
                throw new RuntimeException("File not found: " + filePath);
            }

            Resource resource = new UrlResource(fullPath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("File not found or not readable: " + filePath);
            }

            return resource;

        } catch (IOException e) {
            log.error("Error downloading file: {}", filePath, e);
            throw new RuntimeException("Error downloading file: " + filePath, e);
        }
    }
}
