package com.kiryukhin.mental_health.utils.storages;

import com.kiryukhin.mental_health.configs.YandexCloudConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

@Service
@Log4j2
@RequiredArgsConstructor
public class YandexCloudS3StorageServiceImpl implements StorageService {
    private final S3Client s3Client;
    private final YandexCloudConfig yandexCloudConfig;

    @Override
    public String uploadFile(MultipartFile file, String filePath) {
        try {
            String key = filePath + "/" + UUID.randomUUID() + "_" + Objects.requireNonNull(file.getOriginalFilename()).replace(" ", "_");

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(yandexCloudConfig.getBucketName())
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

            return key;

        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error uploading file");
        }
    }

    @Override
    public String updateFile(MultipartFile file, String paramFileName) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(yandexCloudConfig.getBucketName())
                    .key(paramFileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

            return paramFileName;

        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error uploading file");
        }
    }

    @Override
    public void deleteFile(String key) {
        try {
            s3Client.deleteObject(builder -> builder.bucket(yandexCloudConfig.getBucketName()).key(key));
        } catch (AwsServiceException | SdkClientException e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error deleting file");
        }
    }

    @Override
    public Resource downloadFile(String key) {
        try {
            byte[] fileBytes = s3Client.getObjectAsBytes(builder -> builder.bucket(yandexCloudConfig.getBucketName()).key(key)).asByteArray();

            Path tempFile = Files.createTempFile(UUID.randomUUID().toString(), ".tmp");
            Files.write(tempFile, fileBytes);

            return new UrlResource(tempFile.toUri());

        } catch (IOException e) {
            log.error("Error downloading file from S3: {}", key, e);
            throw new RuntimeException("Error downloading file from S3: " + key, e);
        }
    }

    @Override
    public String getFileUrl(String key) {
        try {
            if (yandexCloudConfig.isClosed()) {
                return generatePresignedUrl(key);
            } else {
                return generateNotPresignedUrl(key);
            }
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            return "";
        }
    }

    private String generateNotPresignedUrl(String key) {
        return s3Client.utilities().getUrl(builder -> builder.bucket(yandexCloudConfig.getBucketName()).key(key)).toExternalForm();
    }

    private String generatePresignedUrl(String key) {
        S3Presigner presigner = S3Presigner.builder()
                .endpointOverride(URI.create(yandexCloudConfig.getStorageEndpoint()))
                .region(Region.of(yandexCloudConfig.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(yandexCloudConfig.getAccessKey(), yandexCloudConfig.getSecretKey())))
                .build();

        try {
            GetObjectRequest objectRequest = GetObjectRequest.builder()
                    .bucket(yandexCloudConfig.getBucketName())
                    .key(key)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(60))
                    .getObjectRequest(objectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
            log.info("Presigned URL: [{}]", presignedRequest.url().toString());
            log.info("HTTP method: [{}]", presignedRequest.httpRequest().method());

            return presignedRequest.url().toExternalForm();
        } finally {
            presigner.close();
        }
    }
}