package com.kiryukhin.mental_health.services.courses;

import com.kiryukhin.mental_health.dtos.responses.AudioCourseResponseDto;
import com.kiryukhin.mental_health.dtos.responses.PaginationResponseDto;
import com.kiryukhin.mental_health.exeptions.EntityNotFoundExceptionCustom;
import com.kiryukhin.mental_health.mappers.AudioCourseMapper;
import com.kiryukhin.mental_health.mappers.PaginationMapper;
import com.kiryukhin.mental_health.models.courses.AudioCourse;
import com.kiryukhin.mental_health.models.courses.CourseTag;
import com.kiryukhin.mental_health.models.courses.CourseTopic;
import com.kiryukhin.mental_health.repositories.courses.AudioCourseRepository;
import com.kiryukhin.mental_health.utils.storages.StorageService;
import com.kiryukhin.mental_health.utils.storages.StorageServiceFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AudioCourseServiceImpl implements AudioCourseService<AudioCourseResponseDto> {

    private final AudioCourseRepository audioCourseRepository;
    private final AudioCourseMapper audioCourseMapper;
    private final PaginationMapper paginationMapper;
    private final StorageServiceFactory storageServiceFactory;
    private final CourseTopicService courseTopicService;
    private final CourseTagService courseTagService;


    @Transactional
    @Override
    public PaginationResponseDto<AudioCourseResponseDto> getAllCourses(Pageable pageable, String filter) {
        Page<AudioCourse> audioCoursePage;

        if (filter != null && !filter.isBlank()) {
            audioCoursePage = audioCourseRepository.findAllByIsPublishedTrueAndDetailsContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrTitleContainingIgnoreCase(
                    pageable, filter);
        } else {
            audioCoursePage = audioCourseRepository.findAllByIsPublishedTrue(pageable);
        }

        Page<AudioCourseResponseDto> audioCoursePageDto = audioCoursePage.map(this::getAudioCourseResponseDtoWithFullImagePreviewUrl);
        return paginationMapper.toPaginationResponseDto(audioCoursePageDto);
    }

    @Override
    public AudioCourseResponseDto getCourseById(UUID id) {
        Optional<AudioCourse> audioCourseOptional = audioCourseRepository.findByIdAndIsPublishedTrue(id);
        return audioCourseOptional.map(this::getAudioCourseResponseDtoWithFullImagePreviewUrl)
                .orElseThrow(() -> new EntityNotFoundExceptionCustom(AudioCourse.class));
    }

    @Override
    public PaginationResponseDto<AudioCourseResponseDto> getCoursesByTagId(Pageable pageable, Long tagId) {
        CourseTag courseTag = courseTagService.getCourseTagById(tagId).orElseThrow(
                () -> new EntityNotFoundExceptionCustom(CourseTag.class));

        Page<AudioCourse> audioCoursePage = audioCourseRepository.findAllByTagsAndIsPublishedTrue(pageable, courseTag);

        Page<AudioCourseResponseDto> audioCoursePageDto = audioCoursePage.map(this::getAudioCourseResponseDtoWithFullImagePreviewUrl);
        return paginationMapper.toPaginationResponseDto(audioCoursePageDto);
    }

    @Override
    public PaginationResponseDto<AudioCourseResponseDto> getCoursesByTopicId(Pageable pageable, Long topicId) {
        CourseTopic courseTopic = courseTopicService.getCourseTopicById(topicId).orElseThrow(
                () -> new EntityNotFoundExceptionCustom(CourseTopic.class));

        Page<AudioCourse> audioCoursePage = audioCourseRepository.findAllByTopicAndIsPublishedTrue(pageable, courseTopic);

        Page<AudioCourseResponseDto> audioCoursePageDto = audioCoursePage.map(this::getAudioCourseResponseDtoWithFullImagePreviewUrl);
        return paginationMapper.toPaginationResponseDto(audioCoursePageDto);
    }

    @Override
    public PaginationResponseDto<AudioCourseResponseDto> searchByStringList(Pageable pageable, List<String> keys) {
        Page<AudioCourse> audioCoursePage = audioCourseRepository.findByKeywordsAndIsPublishedTrue(pageable, keys);

        Page<AudioCourseResponseDto> audioCoursePageDto = audioCoursePage.map(this::getAudioCourseResponseDtoWithFullImagePreviewUrl);
        return paginationMapper.toPaginationResponseDto(audioCoursePageDto);
    }

    private AudioCourseResponseDto getAudioCourseResponseDtoWithFullImagePreviewUrl(AudioCourse audioCourse) {
        StorageService storageService = storageServiceFactory.getStorageService();
        AudioCourseResponseDto dto = audioCourseMapper.toAudioCourseResponseDto(audioCourse);

        if (audioCourse.getImagePreviewUrl() != null) {
            String fullImagePreviewUrl = storageService.getFileUrl(audioCourse.getImagePreviewUrl());
            dto.setImagePreviewUrl(fullImagePreviewUrl);
        }

        return dto;
    }
}
