package com.kiryukhin.mental_health.services.courses;


import com.kiryukhin.mental_health.dtos.requests.AudioCourseRequestDto;

public interface AudioCourseServiceAdmin<ResponseDTO> extends
        CourseService<ResponseDTO>,
        CourseServiceAdmin<AudioCourseRequestDto, ResponseDTO> {
}
