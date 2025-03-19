package com.kiryukhin.mental_health.utils.storages;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface AnyFileStorageService extends StorageService{
    String uploadFile(MultipartFile file, String filePath);
    String updateFile(MultipartFile file, String paramFileName);

    String getFileUrl(String key);

    void deleteFile(String key);

    Resource downloadFile(String key);
}
