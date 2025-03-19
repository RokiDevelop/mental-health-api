package com.kiryukhin.mental_health.configs;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class BunnyStorageConfig {
    @Value("${bunny.stream.libraryId}")
    private String libraryId;

    @Value("${bunny.stream.apiKey}")
    private String apiKey;

    @Value("${bunny.stream.baseUrl}")
    private String baseUrl;
}
