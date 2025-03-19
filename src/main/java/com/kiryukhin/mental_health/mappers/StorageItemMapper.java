package com.kiryukhin.mental_health.mappers;

import com.kiryukhin.mental_health.dtos.StorageItemDto;
import com.kiryukhin.mental_health.models.StorageItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;


@Mapper(componentModel = "spring")
public interface StorageItemMapper {
    StorageItemDto toDto(StorageItem storageItem);

    List<StorageItemDto> toDtoList(List<StorageItem> storageItems);
}