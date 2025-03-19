package com.kiryukhin.mental_health.controllers.courses;

import com.kiryukhin.mental_health.dtos.CourseTopicDto;
import com.kiryukhin.mental_health.services.courses.CourseTopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/course-topics")
@RequiredArgsConstructor
public class CourseTopicController {
    private final CourseTopicService courseTopicService;

    @GetMapping
    public ResponseEntity<List<CourseTopicDto>> getCourseTopics() {
        List<CourseTopicDto> result = courseTopicService.getAllCourseTopics();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseTopicDto> getCourseTopicById(@PathVariable Long id) {
        CourseTopicDto result = courseTopicService.getCourseTopicDtoById(id);
        return ResponseEntity.ok(result);
    }
}
