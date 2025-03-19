package com.kiryukhin.mental_health.controllers.courses;

import com.kiryukhin.mental_health.dtos.requests.AudioCourseRequestDto;
import com.kiryukhin.mental_health.dtos.responses.AudioCourseAdminResponseDto;
import com.kiryukhin.mental_health.dtos.responses.PaginationResponseDto;
import com.kiryukhin.mental_health.services.courses.AudioCourseServiceAdmin;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/audio-courses")
@RequiredArgsConstructor
public class AudioCourseAdminController {

    private final AudioCourseServiceAdmin<AudioCourseAdminResponseDto> audioCourseService;


    @PostMapping
    public ResponseEntity<AudioCourseAdminResponseDto> createAudioCourse(@RequestBody AudioCourseRequestDto audioCourseRequestDto) throws BadRequestException {
        AudioCourseAdminResponseDto responseDto = audioCourseService.createCourse(audioCourseRequestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/upload-preview-image")
    public ResponseEntity<AudioCourseAdminResponseDto> updatePreviewImageAudioCourse(@PathVariable UUID id,
                                                                                     @RequestParam("imagePreview") MultipartFile imagePreview) throws BadRequestException {
        AudioCourseAdminResponseDto responseDto = audioCourseService.updatePreviewImage(id, imagePreview);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<PaginationResponseDto<AudioCourseAdminResponseDto>> getAllAudioCourse(
            @PageableDefault(size = 10, sort = "createdDateTime", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String filter) {
        PaginationResponseDto<AudioCourseAdminResponseDto> responsePageDto = audioCourseService.getAllCourses(pageable, filter);
        return new ResponseEntity<>(responsePageDto, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AudioCourseAdminResponseDto> getAudioCourseById(@PathVariable UUID id) {
        AudioCourseAdminResponseDto responseDto = audioCourseService.getCourseById(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AudioCourseAdminResponseDto> updateAudioCourse(@PathVariable UUID id,
                                                                         @RequestBody AudioCourseRequestDto audioCourseRequestDto) {

        AudioCourseAdminResponseDto responseDto = audioCourseService.updateCourse(id, audioCourseRequestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAudioCourse(@PathVariable UUID id) {
        audioCourseService.deleteCourse(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/by-tag/{tagId}")
    public ResponseEntity<PaginationResponseDto<AudioCourseAdminResponseDto>> getAudioCoursesByTag(
            @PageableDefault(size = 10, sort = "createdDateTime", direction = Sort.Direction.DESC) Pageable pageable,
            @PathVariable Long tagId) {
        PaginationResponseDto<AudioCourseAdminResponseDto> responsePageDto = audioCourseService.getCoursesByTagId(pageable, tagId);
        return new ResponseEntity<>(responsePageDto, HttpStatus.OK);
    }

    @GetMapping("/by-topic/{topicId}")
    public ResponseEntity<PaginationResponseDto<AudioCourseAdminResponseDto>> getAudioCoursesByTopic(
            @PageableDefault(size = 10, sort = "createdDateTime", direction = Sort.Direction.DESC) Pageable pageable,
            @PathVariable Long topicId) {
        PaginationResponseDto<AudioCourseAdminResponseDto> responsePageDto = audioCourseService.getCoursesByTopicId(pageable, topicId);
        return new ResponseEntity<>(responsePageDto, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<PaginationResponseDto<AudioCourseAdminResponseDto>> searchAudioCourses(
            @PageableDefault(size = 10, sort = "createdDateTime", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(name = "search") String keywords) {
        List<String> keys = Arrays.stream(keywords.split("([, .])"))
                .map(String::trim)
                .filter(s -> s.matches("\\d*\\p{L}*\\d*"))
                .toList();
        PaginationResponseDto<AudioCourseAdminResponseDto> responsePageDto = audioCourseService.searchByStringList(pageable, keys);
        return new ResponseEntity<>(responsePageDto, HttpStatus.OK);
    }
}
