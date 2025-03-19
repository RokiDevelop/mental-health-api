package com.kiryukhin.mental_health.repositories.meetings;

import com.kiryukhin.mental_health.models.meetings.GroupPractice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface CustomGroupPracticeRepository<T extends GroupPractice>
        extends CommonCustomMeetingRepository<T> {
    Page<T> findByKeywordsAndIsVisibleTrue(Pageable pageable, List<String> keywords);
}
