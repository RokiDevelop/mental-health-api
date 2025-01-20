package com.kiryukhin.mental_health.repositories.courses;

import com.kiryukhin.mental_health.models.courses.CourseTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseTopicRepository extends JpaRepository<CourseTopic, Long> {
}