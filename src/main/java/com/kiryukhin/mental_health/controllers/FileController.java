package com.kiryukhin.mental_health.controllers;

import com.kiryukhin.mental_health.utils.storages.StorageService;
import com.kiryukhin.mental_health.utils.storages.StorageServiceFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@Log4j2
public class FileController {

    private final StorageServiceFactory storageServiceFactory;


    @GetMapping
    public ResponseEntity<Resource> getFile(@RequestParam(name = "fileKey") String fileKey) {
        StorageService storageService = storageServiceFactory.getStorageService();

        Resource resource = storageService.downloadFile(fileKey);
        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        try {
            String contentType = Files.probeContentType(Paths.get(fileKey));
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (IOException e) {
            log.error("Error determining file content type", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
