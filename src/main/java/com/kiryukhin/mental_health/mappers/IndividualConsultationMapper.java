package com.kiryukhin.mental_health.mappers;

import com.kiryukhin.mental_health.dtos.requests.IndividualConsultationRequestDto;
import com.kiryukhin.mental_health.dtos.responses.IndividualConsultationAdminResponseDto;
import com.kiryukhin.mental_health.dtos.responses.IndividualConsultationResponseDto;
import com.kiryukhin.mental_health.models.User;
import com.kiryukhin.mental_health.models.meetings.IndividualConsultation;
import org.mapstruct.*;

import java.util.Objects;

@Mapper(componentModel = "spring")
public interface IndividualConsultationMapper {

    @Mapping(target = "userIsRegistered", expression = "java(isUserRegistered(entity, currentUser))")
    IndividualConsultationResponseDto entityToResponseDto(IndividualConsultation entity, @Context User currentUser);

    IndividualConsultationAdminResponseDto entityToAdminResponseDto(IndividualConsultation entity);

    IndividualConsultation requestDtoToEntity(IndividualConsultationRequestDto individualConsultationRequestDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePartialFromIndividualConsultationRequestDto(@MappingTarget IndividualConsultation entity, IndividualConsultationRequestDto individualConsultationRequestDto);

    default boolean isUserRegistered(IndividualConsultation entity, @Context User currentUser) {
        return Objects.equals(entity.getUser().getId(), currentUser.getId());
    }
}
