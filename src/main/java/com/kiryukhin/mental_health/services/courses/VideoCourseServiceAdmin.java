package com.kiryukhin.mental_health.services.courses;


import com.kiryukhin.mental_health.dtos.requests.VideoCourseRequestDto;

public interface VideoCourseServiceAdmin<ResponseDTO> extends
        CourseService<ResponseDTO>,
        CourseServiceAdmin<VideoCourseRequestDto, ResponseDTO> {
}
