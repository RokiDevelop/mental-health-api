package com.kiryukhin.mental_health.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

@Data
public class IndividualConsultationAdminResponseDto {
    private UUID id;
    private String title;
    private String details;
    private String description;
    private int duration;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime dateTimeStart;

    @JsonProperty("visible")
    private boolean visible;

    @JsonProperty("started")
    private boolean started;

    @JsonProperty("finished")
    private boolean finished;

    private String comment;
    private String imagePreviewUrl;
    private UserResponseDto user;
}
