package com.kiryukhin.mental_health.dtos.responses;

import com.kiryukhin.mental_health.dtos.StorageItemDto;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class AudioCoursePartResponseDto {
    private Long id;
    private String title;
    private String description;
    private Integer orderIndex;
    private UUID audioCourseId;
    private List<StorageItemDto> storageItems;
}
