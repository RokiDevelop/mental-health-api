package com.kiryukhin.mental_health.mappers;

import com.kiryukhin.mental_health.dtos.CourseTagDto;
import com.kiryukhin.mental_health.dtos.requests.CourseTagRequestDto;
import com.kiryukhin.mental_health.models.courses.CourseTag;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CourseTagMapper {
    CourseTagDto toDto(CourseTag role);

    CourseTag toEntity(CourseTagDto role);
    CourseTag toEntity(CourseTagRequestDto role);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePartial(@MappingTarget CourseTag entity, CourseTagDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePartial(@MappingTarget CourseTag entity, CourseTagRequestDto dto);
}