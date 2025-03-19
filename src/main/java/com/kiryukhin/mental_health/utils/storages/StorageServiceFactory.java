package com.kiryukhin.mental_health.utils.storages;

import com.kiryukhin.mental_health.configs.FileStorageConfig;
import com.kiryukhin.mental_health.models.StorageType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StorageServiceFactory {

    private final FileStorageConfig fileStorageConfig;
    private final YandexAnyStorageService yandexAnyStorageService;
    private final BunnyStreamingStorageService bunnyStreamingStorageService;
    private final LocalAnyStorageService localAnyStorageService;

    public AnyFileStorageService getStorageService() {
        if ("yandex_s3".equalsIgnoreCase(fileStorageConfig.getStorageType())) {
            return yandexAnyStorageService;
        } else if ("local".equalsIgnoreCase(fileStorageConfig.getStorageType())) {
            return localAnyStorageService;
        } else {
            throw new IllegalArgumentException("Invalid storage type: " + fileStorageConfig.getStorageType());
        }
    }

    public <T extends StorageService> T getStorageService(Class<T> storageType) {
        if (storageType.isAssignableFrom(AnyFileStorageService.class)) {
            if ("yandex_s3".equalsIgnoreCase(fileStorageConfig.getStorageType())) {
                return storageType.cast(yandexAnyStorageService);
            } else if ("local".equalsIgnoreCase(fileStorageConfig.getStorageType())) {
                return storageType.cast(localAnyStorageService);
            }
        } else if (storageType.isAssignableFrom(StreamingStorageService.class)) {
            return storageType.cast(bunnyStreamingStorageService);
        }

        throw new IllegalArgumentException("Invalid storage type: " + fileStorageConfig.getStorageType());
    }

    public StorageService getStorageServiceByStorageTypeEnum(StorageType storageType) {
        switch (storageType) {
            case BUNNY -> {
                return bunnyStreamingStorageService;
            }
            case LOCAL -> {
                return localAnyStorageService;
            }
            case YANDEX -> {
                return yandexAnyStorageService;
            }
            default ->
                    throw new IllegalArgumentException(
                            "Invalid storage type: " + fileStorageConfig.getStorageType());
        }
    }

    public YandexAnyStorageService getYandexStorageService() {
        return yandexAnyStorageService;
    }

    public BunnyStreamingStorageService getBunnyStreamingStorageService() {
        return bunnyStreamingStorageService;
    }

    public LocalAnyStorageService getLocalStorageService() {
        return localAnyStorageService;
    }
}
