package com.kiryukhin.mental_health.repositories.courses;

import com.kiryukhin.mental_health.models.courses.AudioCourse;
import com.kiryukhin.mental_health.models.courses.CourseTag;
import com.kiryukhin.mental_health.models.courses.CourseTopic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AudioCourseRepository
        extends JpaRepository<AudioCourse, UUID>,
        CommonCourseRepository<AudioCourse>,
        CustomAudioCourseRepository<AudioCourse> {
    Page<AudioCourse> findAllByTags(Pageable pageable, CourseTag tag);

    Page<AudioCourse> findAllByTopic(Pageable pageable, CourseTopic topic);

    @Query("SELECT ac " +
            "FROM AudioCourse ac " +
            "WHERE LOWER(ac.title) LIKE LOWER(CONCAT('%', :filter, '%')) " +
            "OR LOWER(ac.description) LIKE LOWER(CONCAT('%', :filter, '%')) " +
            "OR LOWER(ac.details) LIKE LOWER(CONCAT('%', :filter, '%'))")
    Page<AudioCourse> findAllFiltered(Pageable pageable, @Param("filter") String filter);

    @Query("SELECT ac " +
            "FROM AudioCourse ac " +
            "WHERE ac.isPublished = true " +
            "AND (LOWER(ac.title) LIKE LOWER(CONCAT('%', :filter, '%')) " +
            "OR LOWER(ac.description) LIKE LOWER(CONCAT('%', :filter, '%')) " +
            "OR LOWER(ac.details) LIKE LOWER(CONCAT('%', :filter, '%')))")
    Page<AudioCourse> findAllByIsPublishedTrueAndDetailsContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrTitleContainingIgnoreCase(
            Pageable pageable, @Param("filter") String filter);

    @Override
    Optional<AudioCourse> findById(UUID uuid);
}
