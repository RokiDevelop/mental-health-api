package com.kiryukhin.mental_health.controllers.courses;

import com.kiryukhin.mental_health.dtos.CourseTagDto;
import com.kiryukhin.mental_health.dtos.requests.CourseTagRequestDto;
import com.kiryukhin.mental_health.services.courses.CourseTagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/course-tags")
@RequiredArgsConstructor
public class CourseTagAdminController {
    private final CourseTagService courseTagService;

    @PostMapping
    public ResponseEntity<CourseTagDto> createCourseTag(@Valid @RequestBody CourseTagRequestDto courseTagDto) {
        CourseTagDto result = courseTagService.createCourseTag(courseTagDto);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseTagDto> updateCourseTag(@PathVariable Long id,
                                                        @Valid @RequestBody CourseTagRequestDto courseTagDto) throws BadRequestException {
        CourseTagDto result = courseTagService.updateCourseTag(id, courseTagDto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCourseTag(@PathVariable Long id) {
        courseTagService.deleteCourseTag(id);
    }

}
