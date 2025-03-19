package com.kiryukhin.mental_health.mappers;

import com.kiryukhin.mental_health.dtos.requests.CreateAndUpdateAudioCoursePartsRequestDto;
import com.kiryukhin.mental_health.dtos.responses.AudioCoursePartResponseDto;
import com.kiryukhin.mental_health.models.courses.AudioCoursePart;
import org.mapstruct.*;


@Mapper(componentModel = "spring")
public interface AudioCoursePartMapper {
    @Mapping(target = "audioCourseId", source = "audioCourse.id")
    AudioCoursePartResponseDto toDto(AudioCoursePart audioCoursePart);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePartial(@MappingTarget AudioCoursePart audioCoursePart, CreateAndUpdateAudioCoursePartsRequestDto requestDto);
}