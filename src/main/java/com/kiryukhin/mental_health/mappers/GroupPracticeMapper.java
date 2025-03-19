package com.kiryukhin.mental_health.mappers;

import com.kiryukhin.mental_health.dtos.requests.GroupPracticeRequestDto;
import com.kiryukhin.mental_health.dtos.responses.GroupPracticeAdminResponseDto;
import com.kiryukhin.mental_health.dtos.responses.GroupPracticeResponseDto;
import com.kiryukhin.mental_health.models.User;
import com.kiryukhin.mental_health.models.meetings.GroupPractice;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface GroupPracticeMapper {

    @Mapping(target = "userIsRegistered", expression = "java(isUserRegistered(entity, currentUser))")
    GroupPracticeResponseDto entityToResponseDto(GroupPractice entity, @Context User currentUser);

    GroupPracticeAdminResponseDto entityToAdminResponseDto(GroupPractice entity);

    GroupPractice requestDtoToEntity(GroupPracticeRequestDto groupPracticeRequestDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePartialFromGroupPracticeRequestDto(@MappingTarget GroupPractice entity, GroupPracticeRequestDto groupPracticeRequestDto);

    default boolean isUserRegistered(GroupPractice entity, @Context User currentUser) {
        return entity.getUsers().contains(currentUser);
    }
}

