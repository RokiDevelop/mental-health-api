package com.kiryukhin.mental_health.repositories;

import com.kiryukhin.mental_health.models.StorageItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StorageItemRepository extends JpaRepository<StorageItem, UUID> {
    Optional<StorageItem> findByIdAndVideoCoursePartId(UUID storageItemId, Long videoPartId);
}
