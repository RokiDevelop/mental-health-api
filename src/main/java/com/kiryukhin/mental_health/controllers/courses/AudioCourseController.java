package com.kiryukhin.mental_health.controllers.courses;

import com.kiryukhin.mental_health.dtos.responses.AudioCourseResponseDto;
import com.kiryukhin.mental_health.dtos.responses.PaginationResponseDto;
import com.kiryukhin.mental_health.services.courses.AudioCourseService;
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
@RequestMapping("/audio-courses")
@RequiredArgsConstructor
public class AudioCourseController {

    private final AudioCourseService<AudioCourseResponseDto> audioCourseService;


    @GetMapping("/{id}")
    public ResponseEntity<AudioCourseResponseDto> getAudioCourseById(@PathVariable UUID id) {
        AudioCourseResponseDto responseDto = audioCourseService.getCourseById(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<PaginationResponseDto<AudioCourseResponseDto>> getAllAudioCourse(
            @PageableDefault(size = 10, sort = "createdDateTime", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String filter) {
        PaginationResponseDto<AudioCourseResponseDto> responsePageDto = audioCourseService.getAllCourses(pageable, filter);
        return new ResponseEntity<>(responsePageDto, HttpStatus.OK);
    }

    @GetMapping("/by-tag/{tagId}")
    public ResponseEntity<PaginationResponseDto<AudioCourseResponseDto>> getAudioCoursesByTag(
            @PageableDefault(size = 10, sort = "createdDateTime", direction = Sort.Direction.DESC) Pageable pageable,
            @PathVariable Long tagId) {
        PaginationResponseDto<AudioCourseResponseDto> responsePageDto = audioCourseService.getCoursesByTagId(pageable, tagId);
        return new ResponseEntity<>(responsePageDto, HttpStatus.OK);
    }

    @GetMapping("/by-topic/{topicId}")
    public ResponseEntity<PaginationResponseDto<AudioCourseResponseDto>> getAudioCoursesByTopic(
            @PageableDefault(size = 10, sort = "createdDateTime", direction = Sort.Direction.DESC) Pageable pageable,
            @PathVariable Long topicId) {
        PaginationResponseDto<AudioCourseResponseDto> responsePageDto = audioCourseService.getCoursesByTopicId(pageable, topicId);
        return new ResponseEntity<>(responsePageDto, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<PaginationResponseDto<AudioCourseResponseDto>> searchAudioCourses(
            @PageableDefault(size = 10, sort = "createdDateTime", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(name = "search") String keywords) {
        List<String> keys = Arrays.stream(keywords.split("([, .])"))
                .map(String::trim)
                .filter(s -> s.matches("\\d*\\p{L}*\\d*"))
                .toList();
        PaginationResponseDto<AudioCourseResponseDto> responsePageDto = audioCourseService.searchByStringList(pageable, keys);
        return new ResponseEntity<>(responsePageDto, HttpStatus.OK);
    }
}
