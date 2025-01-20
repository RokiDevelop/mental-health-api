package com.kiryukhin.mental_health.dtos.requests;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class AudioCourseRequestDto {
    private UUID id;
    private String title;
    private String description;
    private String details;
    private BigDecimal price;
    private BigDecimal priceWithDiscount;
    private boolean discounted;
    private boolean published;
    private Long topicId;
    private Long[] tagIds;
}