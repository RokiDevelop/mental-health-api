package com.kiryukhin.mental_health.services.courses;


import com.kiryukhin.mental_health.models.courses.AudioCourse;

import java.util.UUID;

public interface AudioCourseService<ResponseDTO> extends
        CourseService<ResponseDTO> {
    AudioCourse getAudioCourseEntityById(UUID id);
}
