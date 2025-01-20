package com.kiryukhin.mental_health.services.courses;


import com.kiryukhin.mental_health.dtos.requests.CourseTopicRequestDto;
import com.kiryukhin.mental_health.models.courses.CourseTopic;
import org.apache.coyote.BadRequestException;

import java.util.Optional;

public interface CourseTopicService {
    CourseTopic createCourseTopic(CourseTopic courseTopic);

    Optional<CourseTopic> getCourseTopicById(Long id);

    void deleteCourseTopic(Long id);

    CourseTopic updateCourseTopic(Long id, CourseTopicRequestDto updatedCourseTopic) throws BadRequestException;
}
