package com.kiryukhin.mental_health.dtos.responses;

import com.kiryukhin.mental_health.dtos.CourseTagDto;
import com.kiryukhin.mental_health.dtos.CourseTopicDto;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Data
public class VideoCourseResponseDto {
    private UUID id;
    private String title;
    private String description;
    private String details;
    private BigDecimal price;
    private BigDecimal priceWithDiscount;
    private boolean discounted;
    private String imagePreviewUrl;
    private CourseTopicDto topic;
    private Set<CourseTagDto> tags;
}