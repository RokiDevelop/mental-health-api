package com.kiryukhin.mental_health.configs;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@Getter
@Configuration
public class YandexCloudConfig {
    @Value("${file-storage.yandex_s3.is-closed}")
    private boolean isClosed;

    @Value("${file-storage.yandex_s3.bucket-name}")
    private String bucketName;

    @Value("${file-storage.yandex_s3.access-key}")
    private String accessKey;

    @Value("${file-storage.yandex_s3.secret-key}")
    private String secretKey;

    @Value("${file-storage.yandex_s3.endpoint}")
    private String storageEndpoint;

    @Value("${file-storage.yandex_s3.region}")
    private String region;

    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        return S3Client.builder()
                .region(Region.of(region))
                .endpointOverride(URI.create(storageEndpoint))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();
    }
}
