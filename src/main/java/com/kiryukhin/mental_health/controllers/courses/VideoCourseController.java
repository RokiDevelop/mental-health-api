package com.kiryukhin.mental_health.controllers.courses;

import com.kiryukhin.mental_health.dtos.responses.PaginationResponseDto;
import com.kiryukhin.mental_health.dtos.responses.VideoCourseResponseDto;
import com.kiryukhin.mental_health.services.courses.VideoCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/video-courses")
@RequiredArgsConstructor
public class VideoCourseController {

    private final VideoCourseService<VideoCourseResponseDto> videoCourseService;


    @GetMapping("/")
    public ResponseEntity<PaginationResponseDto<VideoCourseResponseDto>> getAllVideoCourse(
            @PageableDefault(size = 10, sort = "createdDateTime", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String filter) {
        PaginationResponseDto<VideoCourseResponseDto> responseDto = videoCourseService.getAllCourses(pageable, filter);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VideoCourseResponseDto> getVideoCourseById(@PathVariable UUID id) {
        VideoCourseResponseDto responseDto = videoCourseService.getCourseById(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/by-tag/{tagId}")
    public ResponseEntity<PaginationResponseDto<VideoCourseResponseDto>> getVideoCoursesByTag(
            @PageableDefault(size = 10, sort = "createdDateTime", direction = Sort.Direction.DESC) Pageable pageable,
            @PathVariable Long tagId) {
        PaginationResponseDto<VideoCourseResponseDto> responsePageDto = videoCourseService.getCoursesByTagId(pageable, tagId);
        return new ResponseEntity<>(responsePageDto, HttpStatus.OK);
    }

    @GetMapping("/by-topic/{topicId}")
    public ResponseEntity<PaginationResponseDto<VideoCourseResponseDto>> getVideoCoursesByTopic(
            @PageableDefault(size = 10, sort = "createdDateTime", direction = Sort.Direction.DESC) Pageable pageable,
            @PathVariable Long topicId) {
        PaginationResponseDto<VideoCourseResponseDto> responsePageDto = videoCourseService.getCoursesByTopicId(pageable, topicId);
        return new ResponseEntity<>(responsePageDto, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<PaginationResponseDto<VideoCourseResponseDto>> searchVideoCourses(
            @PageableDefault(size = 10, sort = "createdDateTime", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(name = "search") String keywords) {
        List<String> keys = Arrays.stream(keywords.split("([, .])"))
                .map(String::trim)
                .filter(s -> s.matches("\\d*\\p{L}*\\d*"))
                .toList();
        PaginationResponseDto<VideoCourseResponseDto> responsePageDto = videoCourseService.searchByStringList(pageable, keys);
        return new ResponseEntity<>(responsePageDto, HttpStatus.OK);
    }
}
