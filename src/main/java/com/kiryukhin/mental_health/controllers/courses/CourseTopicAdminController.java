package com.kiryukhin.mental_health.controllers.courses;

import com.kiryukhin.mental_health.dtos.CourseTopicDto;
import com.kiryukhin.mental_health.dtos.requests.CourseTopicRequestDto;
import com.kiryukhin.mental_health.services.courses.CourseTopicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/course-topics")
@RequiredArgsConstructor
public class CourseTopicAdminController {
    private final CourseTopicService courseTopicService;

    @PostMapping
    public ResponseEntity<CourseTopicDto> createCourseTopic(@Valid @RequestBody CourseTopicRequestDto courseTopicDto) {
        CourseTopicDto result = courseTopicService.createCourseTopic(courseTopicDto);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseTopicDto> updateCourseTopic(@PathVariable Long id,
                                                            @Valid @RequestBody CourseTopicRequestDto courseTopicDto) throws BadRequestException {
        CourseTopicDto result = courseTopicService.updateCourseTopic(id, courseTopicDto);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCourseTopic(@PathVariable Long id) {
        courseTopicService.deleteCourseTopic(id);
    }
}
