package com.kiryukhin.mental_health.services;

import com.kiryukhin.mental_health.exeptions.EntityNotFoundExceptionCustom;
import com.kiryukhin.mental_health.models.StorageItem;
import com.kiryukhin.mental_health.repositories.StorageItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageItemServiceImpl implements StorageItemService{
    private final StorageItemRepository storageItemRepository;

    @Override
    public StorageItem getStorageItemById(UUID id) {
        return storageItemRepository.findById(id).orElseThrow(() ->{
            log.error("StorageItem with id {} not found", id);
            return new EntityNotFoundExceptionCustom(StorageItem.class, id.toString());
        });
    }

    @Override
    public StorageItem getStorageItemByIdAndVideoPartId(Long videoPartId, UUID storageItemId) {
        return storageItemRepository.findByIdAndVideoCoursePartId(storageItemId, videoPartId).orElseThrow(() ->{
            String errorMessage = String.format("StorageItem with id %s and videoPartId %s not found", storageItemId, videoPartId);
            log.error(errorMessage);
            return new EntityNotFoundExceptionCustom(errorMessage);
        });
    }

    @Override
    public void deleteStorageItemById(UUID id) {
        storageItemRepository.deleteById(id);
    }
}
