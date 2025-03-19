package com.kiryukhin.mental_health.controllers.courses;

import com.kiryukhin.mental_health.dtos.requests.CreateAndUpdateVideoCoursePartsRequestDto;
import com.kiryukhin.mental_health.dtos.requests.SwapCoursePartsRequestDto;
import com.kiryukhin.mental_health.dtos.requests.SwapManyCoursePartsRequestDto;
import com.kiryukhin.mental_health.dtos.responses.PaginationResponseDto;
import com.kiryukhin.mental_health.dtos.responses.VideoCoursePartResponseDto;
import com.kiryukhin.mental_health.services.courses.VideoCoursePartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/admin/video-courses")
@RequiredArgsConstructor
public class VideoCoursePartController {
    private final VideoCoursePartService videoCoursePartService;

    @GetMapping("/{videoCourseId}/parts")
    public ResponseEntity<PaginationResponseDto<VideoCoursePartResponseDto>> getVideoPartsByVideoCourse(
            @PageableDefault(size = 10, sort = "orderIndex", direction = Sort.Direction.DESC) Pageable pageable,
            @PathVariable UUID videoCourseId) {

        PaginationResponseDto<VideoCoursePartResponseDto> responseDto = videoCoursePartService.getPageDtoByCourseId(videoCourseId, pageable);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/{videoCourseId}/parts")
    public ResponseEntity<VideoCoursePartResponseDto> createVideoPart(
            @PathVariable UUID videoCourseId,
            @Valid @RequestBody CreateAndUpdateVideoCoursePartsRequestDto requestDto) {

        VideoCoursePartResponseDto responseDto = videoCoursePartService.createCoursePartAndGetDto(videoCourseId, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/parts/{id}")
    public ResponseEntity<VideoCoursePartResponseDto> getVideoPart(
            @PathVariable("id") Long id) {

        VideoCoursePartResponseDto responseDto = videoCoursePartService.getCoursePartDto(id);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/parts/{id}")
    public ResponseEntity<VideoCoursePartResponseDto> updateVideoPart(
            @PathVariable("id") Long id,
            @Valid @RequestBody CreateAndUpdateVideoCoursePartsRequestDto requestDto) {

        VideoCoursePartResponseDto responseDto = videoCoursePartService.updateCoursePartAndGetDto(id, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/parts/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVideoPart(
            @PathVariable("id") Long id) {

        videoCoursePartService.deleteCoursePart(id);
    }

    @PutMapping("/parts/swap")
    @ResponseStatus(HttpStatus.OK)
    public void swapCourseParts(
            @Valid @RequestBody SwapCoursePartsRequestDto requestDto) {

        videoCoursePartService.swapCourseParts(requestDto.getPartId1(), requestDto.getPartId2());
    }

    @PutMapping("/parts/swap-many")
    @ResponseStatus(HttpStatus.OK)
    public void swapManyCourseParts(
            @Valid @RequestBody SwapManyCoursePartsRequestDto requestDto) {

        videoCoursePartService.changeOrdersToManyCourseParts(requestDto.getPartIds(), requestDto.getOrders());
    }

    @PostMapping("/parts/{videoPartId}/files/upload")
    @ResponseStatus(HttpStatus.OK)
    public void uploadVideoToVideoPart(
            @PathVariable Long videoPartId,
            @RequestParam("file") MultipartFile file) {

        videoCoursePartService.uploadFileToPart(videoPartId, file);
    }

    @DeleteMapping("/parts/{videoPartId}/files/{fileItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVideoFromVideoPart(
            @PathVariable Long videoPartId,
            @PathVariable UUID fileItemId) {

        videoCoursePartService.deleteFileFromPart(videoPartId, fileItemId);
    }
}
