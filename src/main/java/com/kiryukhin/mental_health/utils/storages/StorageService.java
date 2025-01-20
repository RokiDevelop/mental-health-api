package com.kiryukhin.mental_health.utils.storages;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

public interface StorageService {
    String uploadFile(MultipartFile file, String filePath);
    String updateFile(MultipartFile file, String paramFileName);

    String getFileUrl(String key);

    void deleteFile(String key);

    Resource downloadFile(String key);
}
