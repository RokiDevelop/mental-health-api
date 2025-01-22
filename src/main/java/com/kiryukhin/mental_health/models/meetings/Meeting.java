package com.kiryukhin.mental_health.models.meetings;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.ZonedDateTime;
import java.util.UUID;

@MappedSuperclass
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public abstract class Meeting {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", name = "id")
    private UUID id;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "is_visible")
    private boolean isVisible;

    @Column(name = "date_time_start")
    private ZonedDateTime dateTimeStart;

    @Column(name = "duration")
    private int duration;

    @Column(name = "is_started")
    private boolean isStarted;

    @Column(name = "is_finished")
    private boolean isFinished;

    @Column(name = "comment", length = 1000)
    private String comment;
}
