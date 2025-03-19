package com.kiryukhin.mental_health.services.courses;


import com.kiryukhin.mental_health.models.courses.VideoCourse;

import java.util.UUID;

public interface VideoCourseService<ResponseDTO> extends
        CourseService<ResponseDTO>{

    VideoCourse getVideoCourseEntityById(UUID id);
}
