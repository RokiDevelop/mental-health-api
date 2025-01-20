package com.kiryukhin.mental_health.repositories.courses;

import com.kiryukhin.mental_health.models.courses.CourseTag;
import com.kiryukhin.mental_health.models.courses.CourseTopic;
import com.kiryukhin.mental_health.models.courses.VideoCourse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VideoCourseRepository
        extends JpaRepository<VideoCourse, UUID>,
        CommonCourseRepository<VideoCourse>,
        CustomVideoCourseRepository<VideoCourse> {
    Page<VideoCourse> findAllByTags(Pageable pageable, CourseTag tag);

    Page<VideoCourse> findAllByTopic(Pageable pageable, CourseTopic topic);

    @Query("SELECT vc " +
            "FROM VideoCourse vc " +
            "WHERE vc.isPublished = true " +
            "AND (LOWER(vc.title) LIKE LOWER(CONCAT('%', :filter, '%')) " +
            "OR LOWER(vc.description) LIKE LOWER(CONCAT('%', :filter, '%')) " +
            "OR LOWER(vc.details) LIKE LOWER(CONCAT('%', :filter, '%')))")
    Page<VideoCourse> findAllFiltered(Pageable pageable, @Param("filter") String filter);

    Page<VideoCourse> findAllByIsPublishedTrueAndDetailsContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrTitleContainingIgnoreCase(
            Pageable pageable, @Param("filter") String keyword, String keyword2, String keyword3);

    @Override
    Optional<VideoCourse> findById(UUID uuid);

}
