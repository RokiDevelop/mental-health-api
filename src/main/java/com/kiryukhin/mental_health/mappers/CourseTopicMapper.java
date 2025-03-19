package com.kiryukhin.mental_health.mappers;

import com.kiryukhin.mental_health.dtos.CourseTopicDto;
import com.kiryukhin.mental_health.dtos.requests.CourseTopicRequestDto;
import com.kiryukhin.mental_health.models.courses.CourseTopic;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CourseTopicMapper {
    CourseTopicDto toDto(CourseTopic role);

    CourseTopic toEntity(CourseTopicDto role);
    CourseTopic toEntity(CourseTopicRequestDto role);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePartial(@MappingTarget CourseTopic entity, CourseTopicDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePartial(@MappingTarget CourseTopic entity, CourseTopicRequestDto dto);
}