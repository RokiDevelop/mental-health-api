package com.kiryukhin.mental_health.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
public class IndividualConsultationRequestDto {
    private String title;
    private String details;
    private String description;
    private Integer duration;
    private String comment;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime dateTimeStart;

    @JsonProperty("visible")
    private boolean visible;

    @JsonProperty("started")
    private boolean started;

    @JsonProperty("finished")
    private boolean finished;
}
