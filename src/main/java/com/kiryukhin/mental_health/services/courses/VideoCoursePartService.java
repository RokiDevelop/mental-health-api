package com.kiryukhin.mental_health.services.courses;

import com.kiryukhin.mental_health.dtos.requests.CreateAndUpdateVideoCoursePartsRequestDto;
import com.kiryukhin.mental_health.dtos.responses.PaginationResponseDto;
import com.kiryukhin.mental_health.dtos.responses.VideoCoursePartResponseDto;
import com.kiryukhin.mental_health.models.courses.VideoCoursePart;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface VideoCoursePartService {
    VideoCoursePart createCoursePart(UUID videoCourseId,
                                     CreateAndUpdateVideoCoursePartsRequestDto requestDto);

    VideoCoursePartResponseDto updateCoursePartAndGetDto(Long id, CreateAndUpdateVideoCoursePartsRequestDto requestDto);

    VideoCoursePartResponseDto createCoursePartAndGetDto(UUID videoCourseId, CreateAndUpdateVideoCoursePartsRequestDto requestDto);


    @Transactional
    void deleteFileFromPart(Long videoPartId, UUID storageItemId);

    void uploadFileToPart(Long partId, MultipartFile file);


    VideoCoursePartResponseDto getCoursePartDto(Long id);

    void swapCourseParts(Long partId1, Long partId2);

    @Transactional
    void changeOrdersToManyCourseParts(List<Long> partIds, List<Integer> orders);

    @Transactional
    void deleteCoursePart(Long partId);

    PaginationResponseDto<VideoCoursePartResponseDto> getPageDtoByCourseId(UUID videoCourseId, Pageable pageable);
}
