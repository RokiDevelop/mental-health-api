package com.kiryukhin.mental_health.dtos;

import com.kiryukhin.mental_health.models.StorageType;
import lombok.Data;

import java.util.UUID;

@Data
public class StorageItemDto {
    private UUID id;
    private StorageType storageType;
    private String storageObjectId;
    private String url;
}
