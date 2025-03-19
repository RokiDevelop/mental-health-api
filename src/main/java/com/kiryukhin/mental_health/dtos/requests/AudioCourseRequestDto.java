package com.kiryukhin.mental_health.dtos.requests;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AudioCourseRequestDto {
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