package com.kiryukhin.mental_health.services;

import com.kiryukhin.mental_health.models.StorageItem;

import java.util.UUID;

public interface StorageItemService {
    public StorageItem getStorageItemById(UUID id);
    public StorageItem getStorageItemByIdAndVideoPartId(Long videoPartId, UUID storageItemId);

    void deleteStorageItemById(UUID id);
}
