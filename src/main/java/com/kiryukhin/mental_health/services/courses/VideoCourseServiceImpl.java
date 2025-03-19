package com.kiryukhin.mental_health.services.courses;

import com.kiryukhin.mental_health.dtos.responses.PaginationResponseDto;
import com.kiryukhin.mental_health.dtos.responses.VideoCourseResponseDto;
import com.kiryukhin.mental_health.exeptions.EntityNotFoundExceptionCustom;
import com.kiryukhin.mental_health.mappers.PaginationMapper;
import com.kiryukhin.mental_health.mappers.VideoCourseMapper;
import com.kiryukhin.mental_health.models.courses.CourseTag;
import com.kiryukhin.mental_health.models.courses.CourseTopic;
import com.kiryukhin.mental_health.models.courses.VideoCourse;
import com.kiryukhin.mental_health.repositories.courses.VideoCourseRepository;
import com.kiryukhin.mental_health.utils.storages.AnyFileStorageService;
import com.kiryukhin.mental_health.utils.storages.StorageServiceFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VideoCourseServiceImpl implements VideoCourseService<VideoCourseResponseDto> {

    private final VideoCourseRepository videoCourseRepository;
    private final VideoCourseMapper videoCourseMapper;
    private final PaginationMapper paginationMapper;
    private final StorageServiceFactory storageServiceFactory;
    private final CourseTagService courseTagService;
    private final CourseTopicService courseTopicService;


    @Override
    public PaginationResponseDto<VideoCourseResponseDto> getAllCourses(Pageable pageable, String filter) {
        Page<VideoCourse> videoCoursePage;

        if (filter != null && !filter.isBlank()) {
            videoCoursePage =
                    videoCourseRepository.findAllByIsPublishedTrueAndDetailsContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrTitleContainingIgnoreCase(
                            pageable, filter, filter, filter);
        } else {
            videoCoursePage = videoCourseRepository.findAllByIsPublishedTrue(pageable);
        }

        Page<VideoCourseResponseDto> videoCourseDtoPage = videoCoursePage.map(this::getVideoCourseResponseDtoWithFullImagePreviewUrl);
        return paginationMapper.toPaginationResponseDto(videoCourseDtoPage);
    }

    @Override
    public VideoCourseResponseDto getCourseById(UUID id) {
        Optional<VideoCourse> videoCourseOptional = videoCourseRepository.findByIdAndIsPublishedTrue(id);
        return videoCourseOptional.map(this::getVideoCourseResponseDtoWithFullImagePreviewUrl)
                .orElseThrow(() -> new EntityNotFoundExceptionCustom(VideoCourse.class));
    }

    @Override
    public PaginationResponseDto<VideoCourseResponseDto> getCoursesByTagId(Pageable pageable, Long tagId) {
        CourseTag courseTag = courseTagService.getCourseTagById(tagId).orElseThrow(
                () -> new EntityNotFoundExceptionCustom(CourseTag.class));
        ;
        Page<VideoCourse> videoCoursePage = videoCourseRepository.findAllByTagsAndIsPublishedTrue(pageable, courseTag);
        Page<VideoCourseResponseDto> videoCourseDtoPage = videoCoursePage.map(this::getVideoCourseResponseDtoWithFullImagePreviewUrl);
        return paginationMapper.toPaginationResponseDto(videoCourseDtoPage);
    }

    @Override
    public PaginationResponseDto<VideoCourseResponseDto> getCoursesByTopicId(Pageable pageable, Long topicId) {
        CourseTopic courseTopic = courseTopicService.getCourseTopicById(topicId).orElseThrow(
                () -> new EntityNotFoundExceptionCustom(CourseTopic.class));

        Page<VideoCourse> videoCoursePage = videoCourseRepository.findAllByTopicAndIsPublishedTrue(pageable, courseTopic);
        Page<VideoCourseResponseDto> videoCourseDtoPage = videoCoursePage.map(this::getVideoCourseResponseDtoWithFullImagePreviewUrl);
        return paginationMapper.toPaginationResponseDto(videoCourseDtoPage);
    }

    @Override
    public PaginationResponseDto<VideoCourseResponseDto> searchByStringList(Pageable pageable, List<String> keys) {
        Page<VideoCourse> videoCoursePage = videoCourseRepository.findByKeywordsAndIsPublishedTrue(pageable, keys);
        Page<VideoCourseResponseDto> videoCourseDtoPage = videoCoursePage.map(this::getVideoCourseResponseDtoWithFullImagePreviewUrl);
        return paginationMapper.toPaginationResponseDto(videoCourseDtoPage);
    }

    private VideoCourseResponseDto getVideoCourseResponseDtoWithFullImagePreviewUrl(VideoCourse videoCourse) {
        AnyFileStorageService storageService = storageServiceFactory.getStorageService();
        VideoCourseResponseDto dto = videoCourseMapper.toVideoCourseResponseDto(videoCourse);

        if (videoCourse.getImagePreviewUrl() != null) {
            String fullImagePreviewUrl = storageService.getFileUrl(videoCourse.getImagePreviewUrl());
            dto.setImagePreviewUrl(fullImagePreviewUrl);
        }

        return dto;
    }

    @Override
    public VideoCourse getVideoCourseEntityById(UUID id) {
        Optional<VideoCourse> videoCourseOptional = videoCourseRepository.findByIdAndIsPublishedTrue(id);
        return videoCourseOptional.orElseThrow(() -> new EntityNotFoundExceptionCustom(VideoCourse.class));
    }
}

