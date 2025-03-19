package com.kiryukhin.mental_health.mappers;

import com.kiryukhin.mental_health.dtos.requests.CreateAndUpdateVideoCoursePartsRequestDto;
import com.kiryukhin.mental_health.dtos.responses.VideoCoursePartResponseDto;
import com.kiryukhin.mental_health.models.courses.VideoCoursePart;
import org.mapstruct.*;

import java.util.List;


@Mapper(componentModel = "spring")
public interface VideoCoursePartMapper {

    @Mapping(target = "videoCourseId", source = "videoCourse.id")
    VideoCoursePartResponseDto toDto(VideoCoursePart videoCoursePart);

    List<VideoCoursePartResponseDto> toDtoList(List<VideoCoursePart> videoCourseParts);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePartial(@MappingTarget VideoCoursePart entity, CreateAndUpdateVideoCoursePartsRequestDto dto);
}