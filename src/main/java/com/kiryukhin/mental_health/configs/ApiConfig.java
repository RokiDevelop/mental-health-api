package com.kiryukhin.mental_health.configs;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class ApiConfig {
    @Value("${api.base-url}")
    private String apiBaseUrl;

    @Value("${api.version}")
    private String apiVersion;

    @Value("${api.timeout}")
    private int apiTimeout;
}
