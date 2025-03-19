package com.kiryukhin.mental_health.services.courses;

import com.kiryukhin.mental_health.dtos.requests.AudioCourseRequestDto;
import com.kiryukhin.mental_health.dtos.responses.AudioCourseAdminResponseDto;
import com.kiryukhin.mental_health.dtos.responses.PaginationResponseDto;
import com.kiryukhin.mental_health.exeptions.EntityNotFoundExceptionCustom;
import com.kiryukhin.mental_health.mappers.AudioCourseMapper;
import com.kiryukhin.mental_health.mappers.PaginationMapper;
import com.kiryukhin.mental_health.models.courses.AudioCourse;
import com.kiryukhin.mental_health.models.courses.CourseTag;
import com.kiryukhin.mental_health.models.courses.CourseTopic;
import com.kiryukhin.mental_health.repositories.courses.AudioCourseRepository;
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
public class AudioCourseServiceAdminImpl implements AudioCourseServiceAdmin<AudioCourseAdminResponseDto> {

    private final AudioCourseRepository audioCourseRepository;
    private final CourseTopicService courseTopicService;
    private final CourseTagService courseTagService;
    private final AudioCourseMapper audioCourseMapper;
    private final PaginationMapper paginationMapper;
    private final StorageServiceFactory storageServiceFactory;


    @Override
    public PaginationResponseDto<AudioCourseAdminResponseDto> getAllCourses(Pageable pageable, String filter) {
        Page<AudioCourse> audioCoursePage;

        if (filter != null && !filter.isBlank()) {
            audioCoursePage =
                    audioCourseRepository.findAllByDetailsContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrTitleContainingIgnoreCase(
                            pageable, filter, filter, filter);
        } else {
            audioCoursePage = audioCourseRepository.findAll(pageable);
        }

        Page<AudioCourseAdminResponseDto> audioCoursePageDto = audioCoursePage.map(this::getAudioCourseAdminResponseDtoWithFullImagePreviewUrl);
        return paginationMapper.toPaginationResponseDto(audioCoursePageDto);
    }

    @Override
    @Transactional
    public AudioCourseAdminResponseDto createCourse(AudioCourseRequestDto audioCourseDTO) throws BadRequestException {
        AudioCourse audioCourse = new AudioCourse();
        audioCourseMapper.updatePartialFromAudioCourseRequestDto(audioCourse, audioCourseDTO);

        if (audioCourseDTO.getTopicId() == null) {
            throw new BadRequestException("topicId is required!");
        }

        CourseTopic topic = courseTopicService.getCourseTopicById(audioCourseDTO.getTopicId())
                .orElseThrow(() -> new EntityNotFoundExceptionCustom(CourseTopic.class));
        audioCourseMapper.updateTopic(audioCourse, audioCourseDTO, topic);

        if (audioCourseDTO.getTagIds() != null && audioCourseDTO.getTagIds().length > 0) {
            Set<CourseTag> tags = courseTagService.getTagsByIdsArray(audioCourseDTO.getTagIds());
            if (audioCourseDTO.getTagIds().length != tags.size()) {
                throw new EntityNotFoundExceptionCustom(CourseTag.class);
            }
            audioCourseMapper.updateTags(audioCourse, audioCourseDTO, tags);
        }

        audioCourseRepository.save(audioCourse);

        return getAudioCourseAdminResponseDtoWithFullImagePreviewUrl(audioCourse);
    }

    @Override
    public AudioCourseAdminResponseDto updatePreviewImage(UUID id, MultipartFile imagePreview) throws BadRequestException {
        if (imagePreview == null || imagePreview.isEmpty()) {
            throw new BadRequestException("Image file is empty!");
        }
        Optional<AudioCourse> optionalAudioCourse = audioCourseRepository.findById(id);
        AudioCourse audioCourse = optionalAudioCourse.orElseThrow(() ->
                new EntityNotFoundExceptionCustom(AudioCourse.class));

        AnyFileStorageService storageService = storageServiceFactory.getStorageService();
        String key;
        if (audioCourse.getImagePreviewUrl() != null && !audioCourse.getImagePreviewUrl().isBlank()) {
            try {
                key = storageService.updateFile(imagePreview, audioCourse.getImagePreviewUrl());
            } catch (RuntimeException e) {
                key = storageService.uploadFile(imagePreview, "course/audio_course");
            }
        } else {
            key = storageService.uploadFile(imagePreview, "course/audio_course");
        }

        audioCourse.setImagePreviewUrl(key);
        audioCourseRepository.save(audioCourse);

        return getAudioCourseAdminResponseDtoWithFullImagePreviewUrl(audioCourse);
    }

    @Override
    public AudioCourseAdminResponseDto getCourseById(UUID id) {
        Optional<AudioCourse> audioCourseOptional = audioCourseRepository.findById(id);
        return audioCourseOptional.map(this::getAudioCourseAdminResponseDtoWithFullImagePreviewUrl)
                .orElseThrow(() -> new EntityNotFoundExceptionCustom(AudioCourse.class));
    }

    @Override
    public void deleteCourse(UUID id) {
        if (!audioCourseRepository.existsById(id)) {
            throw new EntityNotFoundExceptionCustom(AudioCourse.class);
        }
        audioCourseRepository.deleteById(id);
    }

    @Override
    @Transactional
    public AudioCourseAdminResponseDto updateCourse(UUID id, AudioCourseRequestDto audioCourseRequestDto) {
        Optional<AudioCourse> audioCourseOptional = audioCourseRepository.findById(id);
        AudioCourse audioCourse = audioCourseOptional.orElseThrow(() ->
                new EntityNotFoundExceptionCustom(AudioCourse.class));

        if (audioCourseRequestDto.getTopicId() != null && audioCourseRequestDto.getTopicId() != audioCourse.getTopic().getId()) {
            CourseTopic topic = courseTopicService.getCourseTopicById(audioCourseRequestDto.getTopicId())
                    .orElseThrow(() -> new EntityNotFoundExceptionCustom(CourseTopic.class));
            audioCourseMapper.updateTopic(audioCourse, audioCourseRequestDto, topic);
        }

        if (audioCourseRequestDto.getTagIds() != null && audioCourseRequestDto.getTagIds().length > 0 &&
                !Arrays.equals(audioCourseRequestDto.getTagIds(), audioCourse.getTags().stream().map(CourseTag::getId).toArray(Long[]::new))) {
            Set<CourseTag> tags = courseTagService.getTagsByIdsArray(audioCourseRequestDto.getTagIds());
            if (audioCourseRequestDto.getTagIds().length != tags.size()) {
                throw new EntityNotFoundExceptionCustom(CourseTag.class);
            }

            audioCourseMapper.updateTags(audioCourse, audioCourseRequestDto, tags);
        }

        audioCourseMapper.updatePartialFromAudioCourseRequestDto(audioCourse, audioCourseRequestDto);
        audioCourseRepository.saveAndFlush(audioCourse);

        return getAudioCourseAdminResponseDtoWithFullImagePreviewUrl(audioCourse);
    }

    @Override
    public PaginationResponseDto<AudioCourseAdminResponseDto> getCoursesByTagId(Pageable pageable, Long tagId) {
        CourseTag courseTag = courseTagService.getCourseTagById(tagId).orElseThrow(
                () -> new EntityNotFoundExceptionCustom(CourseTag.class));
        Page<AudioCourse> audioCoursePage = audioCourseRepository.findAllByTags(pageable, courseTag);
        Page<AudioCourseAdminResponseDto> audioCoursePageDto = audioCoursePage.map(this::getAudioCourseAdminResponseDtoWithFullImagePreviewUrl);
        return paginationMapper.toPaginationResponseDto(audioCoursePageDto);
    }

    @Override
    public PaginationResponseDto<AudioCourseAdminResponseDto> getCoursesByTopicId(Pageable pageable, Long topicId) {
        CourseTopic courseTopic = courseTopicService.getCourseTopicById(topicId).orElseThrow(
                () -> new EntityNotFoundExceptionCustom(CourseTopic.class));
        Page<AudioCourse> audioCoursePage = audioCourseRepository.findAllByTopic(pageable, courseTopic);
        Page<AudioCourseAdminResponseDto> audioCoursePageDto = audioCoursePage.map(this::getAudioCourseAdminResponseDtoWithFullImagePreviewUrl);
        return paginationMapper.toPaginationResponseDto(audioCoursePageDto);
    }

    @Override
    public PaginationResponseDto<AudioCourseAdminResponseDto> searchByStringList(Pageable pageable, List<String> keys) {
        Page<AudioCourse> audioCoursePage = audioCourseRepository.findByKeywords(pageable, keys);
        Page<AudioCourseAdminResponseDto> audioCoursePageDto = audioCoursePage.map(this::getAudioCourseAdminResponseDtoWithFullImagePreviewUrl);
        return paginationMapper.toPaginationResponseDto(audioCoursePageDto);
    }

    private AudioCourseAdminResponseDto getAudioCourseAdminResponseDtoWithFullImagePreviewUrl(AudioCourse audioCourse) {
        AnyFileStorageService storageService = storageServiceFactory.getStorageService();
        AudioCourseAdminResponseDto dto = audioCourseMapper.toAudioCourseAdminResponseDto(audioCourse);

        if (audioCourse.getImagePreviewUrl() != null) {
            String fullImagePreviewUrl = storageService.getFileUrl(audioCourse.getImagePreviewUrl());
            dto.setImagePreviewUrl(fullImagePreviewUrl);
        }

        return dto;
    }
}
