package com.kiryukhin.mental_health.services.courses;

import com.kiryukhin.mental_health.dtos.requests.CourseTagRequestDto;
import com.kiryukhin.mental_health.models.courses.CourseTag;
import org.apache.coyote.BadRequestException;

import java.util.Optional;
import java.util.Set;

public interface CourseTagService {
    CourseTag createCourseTag(CourseTag courseTag);

    Optional<CourseTag> getCourseTagById(Long id);

    Set<CourseTag> getTagsByIdsArray(Long[] ids);

    void deleteCourseTag(Long id);

    CourseTag updateCourseTag(Long id, CourseTagRequestDto updatedCourseTag) throws BadRequestException;
}
