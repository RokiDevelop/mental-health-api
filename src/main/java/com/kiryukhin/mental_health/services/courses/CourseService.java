package com.kiryukhin.mental_health.services.courses;

import com.kiryukhin.mental_health.dtos.responses.PaginationResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface CourseService<ResponseDTO> {

    ResponseDTO getCourseById(UUID id);

    PaginationResponseDto<ResponseDTO> searchByStringList(Pageable pageable, List<String> keys);

    PaginationResponseDto<ResponseDTO> getCoursesByTagId(Pageable pageable, Long tagId);

    PaginationResponseDto<ResponseDTO> getCoursesByTopicId(Pageable pageable, Long topicId);

    PaginationResponseDto<ResponseDTO> getAllCourses(Pageable pageable, String filter);
}
