package com.kiryukhin.mental_health.utils.bunny;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class BunnyCdnStorageService {

    @Value("${bunny.cdn.storageZone}")
    private String storageZone;

    @Value("${bunny.cdn.apiKey}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String uploadFile(String localFilePath, String destinationPath) throws Exception {

        byte[] fileBytes = Files.readAllBytes(Paths.get(localFilePath));
        String url = "https://storage.bunnycdn.com/" + storageZone + "/" + destinationPath;
        HttpHeaders headers = new HttpHeaders();
        headers.set("AccessKey", apiKey);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        HttpEntity<byte[]> entity = new HttpEntity<>(fileBytes, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to upload file: " + response.getStatusCode());
        }
        return "https://%s.b-cdn.net/%s".formatted(storageZone, destinationPath);
    }
}