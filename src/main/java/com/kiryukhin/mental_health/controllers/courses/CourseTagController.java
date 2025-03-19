package com.kiryukhin.mental_health.controllers.courses;

import com.kiryukhin.mental_health.dtos.CourseTagDto;
import com.kiryukhin.mental_health.services.courses.CourseTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/course-tags")
@RequiredArgsConstructor
public class CourseTagController {
    private final CourseTagService courseTagService;
    
    @GetMapping
    public ResponseEntity<List<CourseTagDto>> getCourseTags() {
        List<CourseTagDto> result = courseTagService.getAllCourseTags();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseTagDto> getCourseTagById(@PathVariable Long id) {
        CourseTagDto result = courseTagService.getCourseTagDtoById(id);
        return ResponseEntity.ok(result);
    }
}
