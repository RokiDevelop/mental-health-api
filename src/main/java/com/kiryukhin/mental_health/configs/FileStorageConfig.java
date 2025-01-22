package com.kiryukhin.mental_health.configs;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class FileStorageConfig {
    @Value("${file-storage.type}")
    private String storageType;
}
