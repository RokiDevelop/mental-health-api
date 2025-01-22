package com.kiryukhin.mental_health.mappers;

import com.kiryukhin.mental_health.dtos.CourseTagDto;
import com.kiryukhin.mental_health.dtos.CourseTopicDto;
import com.kiryukhin.mental_health.dtos.requests.AudioCourseRequestDto;
import com.kiryukhin.mental_health.dtos.responses.AudioCourseAdminResponseDto;
import com.kiryukhin.mental_health.dtos.responses.AudioCourseResponseDto;
import com.kiryukhin.mental_health.models.courses.AudioCourse;
import com.kiryukhin.mental_health.models.courses.CourseTag;
import com.kiryukhin.mental_health.models.courses.CourseTopic;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface AudioCourseMapper {

    @Mapping(target = "topic", source = "topic", qualifiedByName = "toCourseTopicDto")
    @Mapping(target = "tags", source = "tags", qualifiedByName = "toCourseTagDtoSet")
    AudioCourseResponseDto toAudioCourseResponseDto(AudioCourse entity);

    @Mapping(target = "topic", source = "topic", qualifiedByName = "toCourseTopicDto")
    @Mapping(target = "tags", source = "tags", qualifiedByName = "toCourseTagDtoSet")
    AudioCourseAdminResponseDto toAudioCourseAdminResponseDto(AudioCourse entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "topic", ignore = true)
    @Mapping(target = "tags", ignore = true)
    void updatePartialFromAudioCourseRequestDto(@MappingTarget AudioCourse entity,
                                                AudioCourseRequestDto dto);

    @Named("toCourseTopicDto")
    default CourseTopicDto toCourseTopicDto(CourseTopic topic) {
        if (topic == null) return null;
        CourseTopicDto dto = new CourseTopicDto();
        dto.setId(topic.getId());
        dto.setName(topic.getName().toString());
        return dto;
    }

    @Named("toCourseTagDtoSet")
    default Set<CourseTagDto> toCourseTagDtoSet(Set<CourseTag> tags) {
        if (tags == null) return null;
        return tags.stream()
                .map(tag -> {
                    CourseTagDto dto = new CourseTagDto();
                    dto.setId(tag.getId());
                    dto.setName(tag.getName().toString());
                    return dto;
                })
                .collect(Collectors.toSet());
    }

    default void updateTopicAndTags(AudioCourse entity,
                                    AudioCourseRequestDto dto,
                                    CourseTopic topic,
                                    Set<CourseTag> tags) {
        updateTopic(entity, dto, topic);
        updateTags(entity, dto, tags);
    }

    default void updateTopic(AudioCourse entity,
                             AudioCourseRequestDto dto,
                             CourseTopic topic) {
        if (dto.getTopicId() != null && topic != null) {
            entity.setTopic(topic);
        }
    }

    default void updateTags(AudioCourse entity,
                            AudioCourseRequestDto dto,
                            Set<CourseTag> tags) {
        if (dto.getTagIds() != null && tags != null) {
            entity.setTags(tags);
        }
    }
}