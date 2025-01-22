package com.kiryukhin.mental_health.repositories.courses;

import com.kiryukhin.mental_health.models.courses.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;


public interface CommonCourseRepository<T extends Course>{
    Optional<T> findById(UUID uuid);

    Optional<T> findByIdAndIsPublishedTrue(UUID id);

    Page<T> findAllByIsPublishedTrue(Pageable pageable);

    Page<T> findAllByDetailsContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrTitleContainingIgnoreCase(Pageable pageable, String keyword, String keyword2, String keyword3);

    Page<T> findAllByTagsAndIsPublishedTrue(Pageable pageable, CourseTag tags);

    Page<T> findAllByTopicAndIsPublishedTrue(Pageable pageable, CourseTopic topic);
}
