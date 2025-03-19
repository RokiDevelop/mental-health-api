package com.kiryukhin.mental_health.utils.bunny;

import com.kiryukhin.mental_health.configs.BunnyStorageConfig;
import com.kiryukhin.mental_health.models.courses.VideoResolutionEnum;
import com.kiryukhin.mental_health.utils.storages.BunnyStreamingStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Log4j2
@RequiredArgsConstructor
public class BunnyStreamService implements BunnyStreamingStorageService {
    private final BunnyStorageConfig bunnyStorageConfig;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void deleteVideoStreamFile(String videoGuid) {
        String url = "https://video.bunnycdn.com/library/" + bunnyStorageConfig.getLibraryId() + "/videos/" + videoGuid;
        HttpHeaders headers = new HttpHeaders();
        headers.set("AccessKey", bunnyStorageConfig.getApiKey());
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                new HttpEntity<>(null, headers),
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Failed to delete video. {}", response.getBody());
            throw new RuntimeException("Failed to delete video " + videoGuid + ": " + response.getBody());
        }
    }


    @Override
    public void deleteAudioStreamFile(String guid) {
//        TODO: needs to be implemented
    }


    @Override
    public String createVideoStreamFile(String title) {
        String url = "https://video.bunnycdn.com/library/" + bunnyStorageConfig.getLibraryId() + "/videos";

        HttpHeaders headers = new HttpHeaders();
        headers.set("AccessKey", bunnyStorageConfig.getApiKey());
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("title", title);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                url,
                new HttpEntity<>(body, headers),
                Map.class
        );

        if (!response.getStatusCode().is2xxSuccessful() || !response.getBody().containsKey("guid")) {
            throw new RuntimeException("Failed to create video: " + response.getBody());
        }

        return (String) response.getBody().get("guid");
    }

    @Override
    public String createAudioStreamFile(String title) {
//        TODO: needs to be implemented
        return "";
    }

    @Override
    public String uploadAudioStreamFile(String guid, String path) {
//        TODO: needs to be implemented
        return "";
    }

    @Override
    public String uploadVideoFile(String videoGuid, String filePath) {
        List<VideoResolutionEnum> resolutions = new ArrayList<>();
        String codec = "";
        return uploadVideoFile(videoGuid, filePath, resolutions, codec);
    }

    private String uploadVideoFile(String videoGuid, String filePath, List<VideoResolutionEnum> resolutions, String codec) {
        String url = "https://video.bunnycdn.com/library/" + bunnyStorageConfig.getLibraryId() + "/videos/" + videoGuid;

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("enabledResolutions", String.join(",", resolutions.stream().map(VideoResolutionEnum::getStrResolution).toString()))
                .queryParam("enabledOutputCodecs", codec);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("AccessKey", bunnyStorageConfig.getApiKey());
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            String decodedUrl = URLDecoder.decode(filePath, StandardCharsets.UTF_8);

            ResponseEntity<byte[]> responseToUpload = restTemplate.getForEntity(decodedUrl, byte[].class);
            if (!responseToUpload.getStatusCode().is2xxSuccessful() || responseToUpload.getBody() == null) {
                throw new IOException("Failed to download file from URL: " + decodedUrl);
            }
            byte[] fileBytes = responseToUpload.getBody();

            ResponseEntity<String> response = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.PUT,
                    new HttpEntity<>(fileBytes, headers),
                    String.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Upload failed: " + response.getBody());
            }

            return "https://iframe.mediadelivery.net/embed/" + bunnyStorageConfig.getLibraryId() + "/" + videoGuid;
        } catch (IOException e) {
            throw new RuntimeException("File read error", e);
        }
    }
}