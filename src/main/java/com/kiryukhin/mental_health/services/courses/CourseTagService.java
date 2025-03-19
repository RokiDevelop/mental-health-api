package com.kiryukhin.mental_health.services.courses;

import com.kiryukhin.mental_health.dtos.CourseTagDto;
import com.kiryukhin.mental_health.dtos.requests.CourseTagRequestDto;
import com.kiryukhin.mental_health.models.courses.CourseTag;
import org.apache.coyote.BadRequestException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CourseTagService {
    CourseTagDto createCourseTag(CourseTagRequestDto courseTagDto);

    List<CourseTagDto> getAllCourseTags();

    Optional<CourseTag> getCourseTagById(Long id);

    CourseTagDto getCourseTagDtoById(Long id);

    Set<CourseTag> getTagsByIdsArray(Long[] ids);

    void deleteCourseTag(Long id);

    CourseTagDto updateCourseTag(Long id, CourseTagRequestDto updatedCourseTag) throws BadRequestException;
}
