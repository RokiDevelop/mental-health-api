package com.kiryukhin.mental_health.dtos;

import jakarta.validation.constraints.Null;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BaseDto {

    @Null
    private LocalDateTime createdDateTime;

    @Null
    private LocalDateTime updatedDateTime;

    @Null
    private String createdBy;

    @Null
    private String updatedBy;
}