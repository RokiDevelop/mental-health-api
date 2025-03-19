package com.kiryukhin.mental_health.services.courses;

import com.kiryukhin.mental_health.dtos.requests.VideoCourseRequestDto;
import com.kiryukhin.mental_health.dtos.responses.PaginationResponseDto;
import com.kiryukhin.mental_health.dtos.responses.VideoCourseAdminResponseDto;
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
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
public class VideoCourseServiceAdminImpl implements VideoCourseServiceAdmin<VideoCourseAdminResponseDto> {

    private final VideoCourseRepository videoCourseRepository;
    private final CourseTopicService courseTopicService;
    private final CourseTagService courseTagService;
    private final VideoCourseMapper videoCourseMapper;
    private final PaginationMapper paginationMapper;
    private final StorageServiceFactory storageServiceFactory;


    @Transactional(readOnly = true)
    @Override
    public PaginationResponseDto<VideoCourseAdminResponseDto> getAllCourses(Pageable pageable, String filter) {
        Page<VideoCourse> videoCoursePage;

        if (filter != null && !filter.isBlank()) {
            videoCoursePage = videoCourseRepository.findAllByDetailsContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrTitleContainingIgnoreCase(
                    pageable, filter, filter, filter);
        } else {
            videoCoursePage = videoCourseRepository.findAll(pageable);
        }

        Page<VideoCourseAdminResponseDto> videoCourseDtoPage = videoCoursePage.map(this::getVideoCourseAdminResponseDtoWithFullImagePreviewUrl);
        return paginationMapper.toPaginationResponseDto(videoCourseDtoPage);
    }

    @Override
    @Transactional
    public VideoCourseAdminResponseDto createCourse(VideoCourseRequestDto videoCourseDTO) throws BadRequestException {
        VideoCourse videoCourse = new VideoCourse();
        videoCourseMapper.updatePartialFromVideoCourseRequestDto(videoCourse, videoCourseDTO);

        if (videoCourseDTO.getTopicId() == null) {
            throw new BadRequestException("topicId is required!");
        }

        CourseTopic topic = courseTopicService.getCourseTopicById(videoCourseDTO.getTopicId())
                .orElseThrow(() -> new EntityNotFoundExceptionCustom(CourseTopic.class));
        videoCourseMapper.updateTopic(videoCourse, videoCourseDTO, topic);

        if (videoCourseDTO.getTagIds() != null && videoCourseDTO.getTagIds().length > 0) {
            Set<CourseTag> tags = courseTagService.getTagsByIdsArray(videoCourseDTO.getTagIds());
            if (videoCourseDTO.getTagIds().length != tags.size()) {
                throw new EntityNotFoundExceptionCustom(CourseTag.class);
            }
            videoCourseMapper.updateTags(videoCourse, videoCourseDTO, tags);
        }

        videoCourseRepository.save(videoCourse);

        return getVideoCourseAdminResponseDtoWithFullImagePreviewUrl(videoCourse);
    }

    @Override
    public VideoCourseAdminResponseDto updatePreviewImage(UUID id, MultipartFile imagePreview) throws BadRequestException {
        if (imagePreview == null || imagePreview.isEmpty()) {
            throw new BadRequestException("Image file is empty!");
        }
        Optional<VideoCourse> optionalVideoCourse = videoCourseRepository.findById(id);
        VideoCourse videoCourse = optionalVideoCourse.orElseThrow(() ->
                new EntityNotFoundExceptionCustom(VideoCourse.class));

        AnyFileStorageService storageService = storageServiceFactory.getStorageService();
        String key;
        if (videoCourse.getImagePreviewUrl() != null && !videoCourse.getImagePreviewUrl().isBlank()) {
            try {
                key = storageService.updateFile(imagePreview, videoCourse.getImagePreviewUrl());
            } catch (RuntimeException e) {
                key = storageService.uploadFile(imagePreview, "course/audio_course");
            }
        } else {
            key = storageService.uploadFile(imagePreview, "course/audio_course");
        }

        videoCourse.setImagePreviewUrl(key);
        videoCourseRepository.save(videoCourse);

        return getVideoCourseAdminResponseDtoWithFullImagePreviewUrl(videoCourse);
    }

    @Override
    public VideoCourseAdminResponseDto getCourseById(UUID id) {
        Optional<VideoCourse> videoCourseOptional = videoCourseRepository.findById(id);
        return videoCourseOptional.map(this::getVideoCourseAdminResponseDtoWithFullImagePreviewUrl)
                .orElseThrow(() -> new EntityNotFoundExceptionCustom(VideoCourse.class));
    }

    @Override
    public void deleteCourse(UUID id) {
        if (!videoCourseRepository.existsById(id)) {
            throw new EntityNotFoundExceptionCustom(VideoCourse.class);
        }
        videoCourseRepository.deleteById(id);
    }

    @Override
    @Transactional
    public VideoCourseAdminResponseDto updateCourse(UUID id, VideoCourseRequestDto videoCourseRequestDto) {
        Optional<VideoCourse> videoCourseOptional = videoCourseRepository.findById(id);
        VideoCourse videoCourse = videoCourseOptional.orElseThrow(() ->
                new EntityNotFoundExceptionCustom(VideoCourse.class));

        if (videoCourseRequestDto.getTopicId() != null && videoCourseRequestDto.getTopicId() != videoCourse.getTopic().getId()) {
            CourseTopic topic = courseTopicService.getCourseTopicById(videoCourseRequestDto.getTopicId())
                    .orElseThrow(() -> new EntityNotFoundExceptionCustom(CourseTopic.class));
            videoCourseMapper.updateTopic(videoCourse, videoCourseRequestDto, topic);
        }

        if (videoCourseRequestDto.getTagIds() != null && videoCourseRequestDto.getTagIds().length > 0 &&
                !Arrays.equals(videoCourseRequestDto.getTagIds(), videoCourse.getTags().stream().map(CourseTag::getId).toArray(Long[]::new))) {
            Set<CourseTag> tags = courseTagService.getTagsByIdsArray(videoCourseRequestDto.getTagIds());
            if (videoCourseRequestDto.getTagIds().length != tags.size()) {
                throw new EntityNotFoundExceptionCustom(CourseTag.class);
            }

            videoCourseMapper.updateTags(videoCourse, videoCourseRequestDto, tags);
        }

        videoCourseMapper.updatePartialFromVideoCourseRequestDto(videoCourse, videoCourseRequestDto);
        videoCourseRepository.saveAndFlush(videoCourse);

        return getVideoCourseAdminResponseDtoWithFullImagePreviewUrl(videoCourse);
    }

    @Override
    public PaginationResponseDto<VideoCourseAdminResponseDto> getCoursesByTagId(Pageable pageable, Long tagId) {
        CourseTag courseTag = courseTagService.getCourseTagById(tagId).orElseThrow(
                () -> new EntityNotFoundExceptionCustom(CourseTag.class));
        Page<VideoCourse> videoCoursePage = videoCourseRepository.findAllByTags(pageable, courseTag);
        Page<VideoCourseAdminResponseDto> videoCourseDtoPage = videoCoursePage.map(this::getVideoCourseAdminResponseDtoWithFullImagePreviewUrl);
        return paginationMapper.toPaginationResponseDto(videoCourseDtoPage);
    }

    @Override
    public PaginationResponseDto<VideoCourseAdminResponseDto> getCoursesByTopicId(Pageable pageable, Long topicId) {
        CourseTopic courseTopic = courseTopicService.getCourseTopicById(topicId).orElseThrow(
                () -> new EntityNotFoundExceptionCustom(CourseTopic.class));
        Page<VideoCourse> videoCoursePage = videoCourseRepository.findAllByTopic(pageable, courseTopic);
        Page<VideoCourseAdminResponseDto> videoCourseDtoPage = videoCoursePage.map(this::getVideoCourseAdminResponseDtoWithFullImagePreviewUrl);
        return paginationMapper.toPaginationResponseDto(videoCourseDtoPage);
    }

    @Override
    public PaginationResponseDto<VideoCourseAdminResponseDto> searchByStringList(Pageable pageable, List<String> keys) {
        Page<VideoCourse> videoCoursePage = videoCourseRepository.findByKeywords(pageable, keys);
        Page<VideoCourseAdminResponseDto> videoCourseDtoPage = videoCoursePage.map(this::getVideoCourseAdminResponseDtoWithFullImagePreviewUrl);
        return paginationMapper.toPaginationResponseDto(videoCourseDtoPage);
    }

    private VideoCourseAdminResponseDto getVideoCourseAdminResponseDtoWithFullImagePreviewUrl(VideoCourse videoCourse) {
        AnyFileStorageService storageService = storageServiceFactory.getStorageService();
        VideoCourseAdminResponseDto dto = videoCourseMapper.toVideoCourseAdminResponseDto(videoCourse);

        if (videoCourse.getImagePreviewUrl() != null) {
            String fullImagePreviewUrl = storageService.getFileUrl(videoCourse.getImagePreviewUrl());
            dto.setImagePreviewUrl(fullImagePreviewUrl);
        }

        return dto;
    }
}
