package com.kiryukhin.mental_health.controllers.courses;

import com.kiryukhin.mental_health.dtos.requests.VideoCourseRequestDto;
import com.kiryukhin.mental_health.dtos.responses.PaginationResponseDto;
import com.kiryukhin.mental_health.dtos.responses.VideoCourseAdminResponseDto;
import com.kiryukhin.mental_health.services.courses.VideoCourseServiceAdmin;
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
@RequestMapping("/admin/video-courses")
@RequiredArgsConstructor
public class VideoCourseAdminController {

    private final VideoCourseServiceAdmin<VideoCourseAdminResponseDto> videoCourseService;


    @PostMapping("/")
    public ResponseEntity<VideoCourseAdminResponseDto> createVideoCourse(@RequestBody VideoCourseRequestDto videoCourseRequestDto) throws BadRequestException {
        VideoCourseAdminResponseDto responseDto = videoCourseService.createCourse(videoCourseRequestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/upload-preview-image")
    public ResponseEntity<VideoCourseAdminResponseDto> updatePreviewImageAudioCourse(@PathVariable UUID id,
                                                           @RequestParam("imagePreview") MultipartFile imagePreview) throws BadRequestException {
        VideoCourseAdminResponseDto responseDto = videoCourseService.updatePreviewImage(id, imagePreview);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/")
    public ResponseEntity<PaginationResponseDto<VideoCourseAdminResponseDto>> getAllVideoCourses(
            @PageableDefault(size = 10, sort = "createdDateTime", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String filter) {
        PaginationResponseDto<VideoCourseAdminResponseDto> responsePageDto = videoCourseService.getAllCourses(pageable, filter);
        return new ResponseEntity<>(responsePageDto, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VideoCourseAdminResponseDto> getVideoCourseById(@PathVariable UUID id) {
        VideoCourseAdminResponseDto responseDto = videoCourseService.getCourseById(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VideoCourseAdminResponseDto> updateVideoCourse(@PathVariable UUID id,
                                               @RequestBody VideoCourseRequestDto videoCourseRequestDto)
            throws BadRequestException {
        if (!id.equals(videoCourseRequestDto.getId())) {
            throw new BadRequestException("IDs do not match");
        }

        VideoCourseAdminResponseDto responseDto = videoCourseService.updateCourse(id, videoCourseRequestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVideoCourse(@PathVariable UUID id) {
        videoCourseService.deleteCourse(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/by-tag/{tagId}")
    public ResponseEntity<PaginationResponseDto<VideoCourseAdminResponseDto>> getVideoCoursesByTag(
            @PageableDefault(size = 10, sort = "createdDateTime", direction = Sort.Direction.DESC) Pageable pageable,
            @PathVariable Long tagId) {
        PaginationResponseDto<VideoCourseAdminResponseDto> responsePageDto = videoCourseService.getCoursesByTagId(pageable, tagId);
        return new ResponseEntity<>(responsePageDto, HttpStatus.OK);
    }

    @GetMapping("/by-topic/{topicId}")
    public ResponseEntity<PaginationResponseDto<VideoCourseAdminResponseDto>> getVideoCoursesByTopic(
            @PageableDefault(size = 10, sort = "createdDateTime", direction = Sort.Direction.DESC) Pageable pageable,
            @PathVariable Long topicId) {
        PaginationResponseDto<VideoCourseAdminResponseDto> responsePageDto = videoCourseService.getCoursesByTopicId(pageable, topicId);
        return new ResponseEntity<>(responsePageDto, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<PaginationResponseDto<VideoCourseAdminResponseDto>> searchVideoCourses(
            @PageableDefault(size = 10, sort = "createdDateTime", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(name = "search") String keywords) {
        List<String> keys = Arrays.stream(keywords.split("([, .])"))
                .map(String::trim)
                .filter(s -> s.matches("\\d*\\p{L}*\\d*"))
                .toList();
        PaginationResponseDto<VideoCourseAdminResponseDto> responsePageDto = videoCourseService.searchByStringList(pageable, keys);
        return new ResponseEntity<>(responsePageDto, HttpStatus.OK);
    }
}