package com.kiryukhin.mental_health.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
public class GroupPracticeResponseDto {
    private UUID id;
    private String title;
    private String details;
    private String description;
    private int duration;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime dateTimeStart;

    @JsonProperty("started")
    private boolean started;

    @JsonProperty("finished")
    private boolean finished;

    private String imagePreviewUrl;

    @JsonProperty("user_is_registered")
    private boolean userIsRegistered;
}
