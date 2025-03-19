package com.kiryukhin.mental_health.repositories.meetings;

import com.kiryukhin.mental_health.models.meetings.IndividualConsultation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface CustomIndividualConsultationRepository<T extends IndividualConsultation>
        extends CommonCustomMeetingRepository<T> {
    Page<T> findByKeywordsAndIsVisibleTrueAndIsReservedFalse(Pageable pageable, List<String> keywords);
}
