package com.kiryukhin.mental_health.services.courses;

import com.kiryukhin.mental_health.dtos.CourseTopicDto;
import com.kiryukhin.mental_health.dtos.requests.CourseTopicRequestDto;
import com.kiryukhin.mental_health.models.courses.CourseTopic;
import org.apache.coyote.BadRequestException;

import java.util.List;
import java.util.Optional;

public interface CourseTopicService {
    CourseTopicDto createCourseTopic(CourseTopicRequestDto courseTopic);

    Optional<CourseTopic> getCourseTopicById(Long id);
    CourseTopicDto getCourseTopicDtoById(Long id);

    List<CourseTopicDto> getAllCourseTopics();

    void deleteCourseTopic(Long id);

    CourseTopicDto updateCourseTopic(Long id, CourseTopicRequestDto updatedCourseTopic) throws BadRequestException;
}
