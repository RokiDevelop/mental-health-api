package com.kiryukhin.mental_health.controllers.courses;

import com.kiryukhin.mental_health.dtos.requests.CreateAndUpdateAudioCoursePartsRequestDto;
import com.kiryukhin.mental_health.dtos.requests.SwapCoursePartsRequestDto;
import com.kiryukhin.mental_health.dtos.requests.SwapManyCoursePartsRequestDto;
import com.kiryukhin.mental_health.dtos.responses.AudioCoursePartResponseDto;
import com.kiryukhin.mental_health.dtos.responses.PaginationResponseDto;
import com.kiryukhin.mental_health.services.courses.AudioCoursePartService;
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
@RequestMapping("/admin/audio-courses")
@RequiredArgsConstructor
public class AudioCoursePartController {
    private final AudioCoursePartService audioCoursePartService;

    @GetMapping("/{audioCourseId}/parts")
    public ResponseEntity<PaginationResponseDto<AudioCoursePartResponseDto>> getAudioPartsByAudioCourse(
            @PageableDefault(size = 10, sort = "orderIndex", direction = Sort.Direction.DESC) Pageable pageable,
            @PathVariable UUID audioCourseId) {

        PaginationResponseDto<AudioCoursePartResponseDto> responseDto = audioCoursePartService.getPageDtoByCourseId(audioCourseId, pageable);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/{audioCourseId}/parts")
    public ResponseEntity<AudioCoursePartResponseDto> createAudioPart(
            @PathVariable UUID audioCourseId,
            @Valid @RequestBody CreateAndUpdateAudioCoursePartsRequestDto requestDto) {

        AudioCoursePartResponseDto responseDto = audioCoursePartService.createCoursePartAndGetDto(audioCourseId, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/parts/{id}")
    public ResponseEntity<AudioCoursePartResponseDto> getAudioPart(
            @PathVariable("id") Long id) {

        AudioCoursePartResponseDto responseDto = audioCoursePartService.getCoursePartDto(id);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/parts/{id}")
    public ResponseEntity<AudioCoursePartResponseDto> updateAudioPart(
            @PathVariable("id") Long id,
            @Valid @RequestBody CreateAndUpdateAudioCoursePartsRequestDto requestDto) {

        AudioCoursePartResponseDto responseDto = audioCoursePartService.updateCoursePartAndGetDto(id, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/parts/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAudioPart(
            @PathVariable("id") Long id) {

        audioCoursePartService.deleteCoursePart(id);
    }

    @PutMapping("/parts/swap")
    @ResponseStatus(HttpStatus.OK)
    public void swapCourseParts(
            @Valid @RequestBody SwapCoursePartsRequestDto requestDto) {

        audioCoursePartService.swapCourseParts(requestDto.getPartId1(), requestDto.getPartId2());
    }

    @PutMapping("/parts/swap-many")
    @ResponseStatus(HttpStatus.OK)
    public void swapManyCourseParts(
            @Valid @RequestBody SwapManyCoursePartsRequestDto requestDto) {

        audioCoursePartService.changeOrdersToManyCourseParts(requestDto.getPartIds(), requestDto.getOrders());
    }

    @PostMapping("/parts/{audioPartId}/files/upload")
    @ResponseStatus(HttpStatus.OK)
    public void uploadAudioToAudioPart(
            @PathVariable Long audioPartId,
            @RequestParam("file") MultipartFile file) {

        audioCoursePartService.uploadFileToPart(audioPartId, file);
    }

    @DeleteMapping("/parts/{audioPartId}/files/{fileItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAudioFromAudioPart(
            @PathVariable Long audioPartId,
            @PathVariable UUID fileItemId) {

        audioCoursePartService.deleteFileFromPart(audioPartId, fileItemId);
    }
}
