package com.kiryukhin.mental_health.utils.storages;

import com.kiryukhin.mental_health.configs.FileStorageConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StorageServiceFactory {

    private final FileStorageConfig fileStorageConfig;
    private final YandexCloudS3StorageServiceImpl s3StorageService;
    private final LocalStorageServiceImpl localStorageService;

    public StorageService getStorageService() {
        if ("yandex_s3".equalsIgnoreCase(fileStorageConfig.getStorageType())) {
            return s3StorageService;
        } else if ("local".equalsIgnoreCase(fileStorageConfig.getStorageType())) {
            return localStorageService;
        } else {
            throw new IllegalArgumentException("Invalid storage type: " + fileStorageConfig.getStorageType());
        }
    }
}
