package com.kiryukhin.mental_health.dtos;

import jakarta.validation.constraints.Null;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseDto {

    @Null private LocalDateTime createdDateTime;

    @Null private LocalDateTime updatedDateTime;

    @Null private String createdBy;

    @Null private String updatedBy;
}