package com.kiryukhin.mental_health.repositories.courses;

import com.kiryukhin.mental_health.models.courses.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommonCustomCourseRepository<T extends Course> {
    Page<T> findByKeywords(Pageable pageable, List<String> keywords);

    Page<T> findByKeywordsAndIsPublishedTrue(Pageable pageable, List<String> Keywords);
}
