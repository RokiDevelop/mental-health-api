package com.kiryukhin.mental_health.dtos.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateAndUpdateVideoCoursePartsRequestDto {
    @NotNull
    private String title;

    @NotNull
    private String description;
}
