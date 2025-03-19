package com.kiryukhin.mental_health.services.courses;

import com.kiryukhin.mental_health.dtos.requests.CreateAndUpdateAudioCoursePartsRequestDto;
import com.kiryukhin.mental_health.dtos.responses.AudioCoursePartResponseDto;
import com.kiryukhin.mental_health.dtos.responses.PaginationResponseDto;
import com.kiryukhin.mental_health.models.courses.AudioCoursePart;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;


public interface AudioCoursePartService {
    AudioCoursePart createCoursePart(UUID audioCourseId, CreateAndUpdateAudioCoursePartsRequestDto requestDto);

    AudioCoursePartResponseDto updateCoursePartAndGetDto(Long id, CreateAndUpdateAudioCoursePartsRequestDto requestDto);

    AudioCoursePartResponseDto createCoursePartAndGetDto(UUID audioCourseId, CreateAndUpdateAudioCoursePartsRequestDto requestDto);

    @Transactional
    void deleteFileFromPart(Long audioPartId, UUID storageItemId);

    void uploadFileToPart(Long partId, MultipartFile file);

    AudioCoursePartResponseDto getCoursePartDto(Long id);

    void swapCourseParts(Long partId1, Long partId2);

    @Transactional
    void changeOrdersToManyCourseParts(List<Long> partIds, List<Integer> orders);

    @Transactional
    void deleteCoursePart(Long partId);

    PaginationResponseDto<AudioCoursePartResponseDto> getPageDtoByCourseId(UUID audioCourseId, Pageable pageable);
}
