package com.kiryukhin.mental_health.repositories.meetings;

import com.kiryukhin.mental_health.models.meetings.Meeting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface CommonCustomMeetingRepository<T extends Meeting>  {
    Page<T> findByKeywords(Pageable pageable, List<String> keywords);
}
