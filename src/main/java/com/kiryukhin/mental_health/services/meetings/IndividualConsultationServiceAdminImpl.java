package com.kiryukhin.mental_health.services.meetings;

import com.kiryukhin.mental_health.dtos.GroupedMeetingDto;
import com.kiryukhin.mental_health.dtos.requests.IndividualConsultationRequestDto;
import com.kiryukhin.mental_health.dtos.responses.IndividualConsultationAdminResponseDto;
import com.kiryukhin.mental_health.dtos.responses.PaginationResponseDto;
import com.kiryukhin.mental_health.exeptions.EntityNotFoundExceptionCustom;
import com.kiryukhin.mental_health.mappers.IndividualConsultationMapper;
import com.kiryukhin.mental_health.mappers.PaginationMapper;
import com.kiryukhin.mental_health.models.meetings.IndividualConsultation;
import com.kiryukhin.mental_health.repositories.meetings.IndividualConsultationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IndividualConsultationServiceAdminImpl implements IndividualConsultationServiceAdmin<IndividualConsultationAdminResponseDto> {

    private final IndividualConsultationRepository individualConsultationRepository;
    private final IndividualConsultationMapper individualConsultationMapper;
    private final PaginationMapper paginationMapper;


    @Override
    public IndividualConsultationAdminResponseDto getMeeting(UUID id) {
        Optional<IndividualConsultation> optionalIndividualConsultation = individualConsultationRepository.findById(id);
        IndividualConsultation individualConsultation = optionalIndividualConsultation.orElseThrow(() ->
                new EntityNotFoundExceptionCustom(IndividualConsultation.class));

        return individualConsultationMapper.entityToAdminResponseDto(individualConsultation);
    }

    @Override
    public IndividualConsultationAdminResponseDto createMeeting(IndividualConsultationRequestDto individualConsultationRequestDto) {
        IndividualConsultation individualConsultation =
                individualConsultationMapper.requestDtoToEntity(individualConsultationRequestDto);
        individualConsultation = individualConsultationRepository.save(individualConsultation);
        return individualConsultationMapper.entityToAdminResponseDto(individualConsultation);
    }

    @Override
    public void deleteMeeting(UUID id) {
        Optional<IndividualConsultation> optionalIndividualConsultation = individualConsultationRepository.findById(id);
        IndividualConsultation individualConsultation = optionalIndividualConsultation.orElseThrow(() ->
                new EntityNotFoundExceptionCustom(IndividualConsultation.class));

        individualConsultationRepository.delete(individualConsultation);
    }

    @Override
    public IndividualConsultationAdminResponseDto updateMeeting(UUID id, IndividualConsultationRequestDto individualConsultationRequestDto) {
        Optional<IndividualConsultation> optionalIndividualConsultation = individualConsultationRepository.findById(id);
        IndividualConsultation individualConsultation = optionalIndividualConsultation.orElseThrow(() ->
                new EntityNotFoundExceptionCustom(IndividualConsultation.class));

        individualConsultationMapper.updatePartialFromIndividualConsultationRequestDto(individualConsultation, individualConsultationRequestDto);
        individualConsultationRepository.save(individualConsultation);

        return individualConsultationMapper.entityToAdminResponseDto(individualConsultation);
    }

    @Override
    public PaginationResponseDto<IndividualConsultationAdminResponseDto> getAllMeetings(Pageable pageable, String filter) {
        Page<IndividualConsultation> individualConsultationList;

        if (filter != null && !filter.isBlank()) {
            individualConsultationList = individualConsultationRepository.findAllByDetailsContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrTitleContainingIgnoreCase(
                    pageable, filter, filter, filter);
        } else {
            individualConsultationList = individualConsultationRepository.findAll(pageable);
        }

        Page<IndividualConsultationAdminResponseDto> individualConsultationDtoPage = individualConsultationList.map(individualConsultationMapper::entityToAdminResponseDto);
        return paginationMapper.toPaginationResponseDto(individualConsultationDtoPage);
    }

    @Override
    public PaginationResponseDto<IndividualConsultationAdminResponseDto> searchByStringList(Pageable pageable, List<String> keys) {
        Page<IndividualConsultation> individualConsultationList =
                individualConsultationRepository.findByKeywords(pageable, keys);
        Page<IndividualConsultationAdminResponseDto> individualConsultationDtoPage = individualConsultationList.map(individualConsultationMapper::entityToAdminResponseDto);
        return paginationMapper.toPaginationResponseDto(individualConsultationDtoPage);
    }

    @Override
    public List<GroupedMeetingDto<IndividualConsultationAdminResponseDto>> getMeetingsGroupedByDate(ZonedDateTime startDate, ZonedDateTime endDate) {
        if (startDate == null) {
            startDate = YearMonth.now().atDay(1).atStartOfDay(ZoneId.systemDefault());
        }
        if (endDate == null) {
            endDate = YearMonth.now().atEndOfMonth().atTime(23, 59, 59).atZone(ZoneId.systemDefault());
        }

        List<IndividualConsultation> meetings = individualConsultationRepository.findAllByDateTimeStartBetween(startDate, endDate);

        Map<LocalDate, List<IndividualConsultationAdminResponseDto>> groupedByDate = meetings.stream()
                .map(individualConsultationMapper::entityToAdminResponseDto)
                .collect(Collectors.groupingBy(dto -> dto.getDateTimeStart().toLocalDate()));

        return groupedByDate.entrySet().stream()
                .sorted((entry1, entry2) -> entry1.getKey().compareTo(entry2.getKey()))
                .map(entry -> new GroupedMeetingDto<>(entry.getKey(), entry.getValue()))
                .toList();
    }
}
