package com.kiryukhin.mental_health.services.courses;

import org.apache.coyote.BadRequestException;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface CourseServiceAdmin<RequestDTO, ResponseDTO> {
    ResponseDTO createCourse(RequestDTO requestDto) throws BadRequestException;

    void deleteCourse(UUID id);

    ResponseDTO updateCourse(UUID id, RequestDTO requestDto);

    ResponseDTO updatePreviewImage(UUID id, MultipartFile imagePreview) throws BadRequestException;
}
