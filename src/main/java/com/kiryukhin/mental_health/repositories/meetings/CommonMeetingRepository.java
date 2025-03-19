package com.kiryukhin.mental_health.repositories.meetings;

import com.kiryukhin.mental_health.models.meetings.Meeting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface CommonMeetingRepository<T extends Meeting> {
    Optional<T> findById(UUID uuid);

    Optional<T> findByIdAndIsVisibleTrue(UUID id);

    Optional<T> findByIdAndIsVisibleTrueAndIsFinishedFalse(UUID id);

    Page<T> findAllByDetailsContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrTitleContainingIgnoreCase(
            Pageable pageable, String filter1, String filter2, String filter3);

    List<T> findAllByDateTimeStartBetween(ZonedDateTime startDate, ZonedDateTime endDate);

    List<T> findAllByDateTimeStartBetweenAndIsVisibleTrue(ZonedDateTime startDate, ZonedDateTime endDate);
}
