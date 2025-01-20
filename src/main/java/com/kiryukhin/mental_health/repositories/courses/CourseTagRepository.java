package com.kiryukhin.mental_health.repositories.courses;

import com.kiryukhin.mental_health.models.courses.CourseTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface CourseTagRepository extends JpaRepository<CourseTag, Long> {
    Set<CourseTag> findByIdIn(Long[] ids);
}
