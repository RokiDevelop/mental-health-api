package com.kiryukhin.mental_health.dtos.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SwapCoursePartsRequestDto {
    @NotNull
    private Long partId1;

    @NotNull
    private Long partId2;
}
