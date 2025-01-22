package com.kiryukhin.mental_health.configs;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class LocalStorageConfig {
    @Value("${file-storage.local.path}")
    private String localStoragePath;

    @Value("${file-storage.local.endpoint}")
    private String filesEndpoint;
}
