package com.kiryukhin.mental_health.dtos.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SwapManyCoursePartsRequestDto {
    @NotNull
    private List<Long> partIds;

    @NotNull
    private List<Integer> orders;
}
