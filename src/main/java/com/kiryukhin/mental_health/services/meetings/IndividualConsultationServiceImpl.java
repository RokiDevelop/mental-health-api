package com.kiryukhin.mental_health.services.meetings;

import com.kiryukhin.mental_health.dtos.GroupedMeetingDto;
import com.kiryukhin.mental_health.dtos.responses.IndividualConsultationResponseDto;
import com.kiryukhin.mental_health.dtos.responses.PaginationResponseDto;
import com.kiryukhin.mental_health.exeptions.EntityNotFoundExceptionCustom;
import com.kiryukhin.mental_health.mappers.IndividualConsultationMapper;
import com.kiryukhin.mental_health.mappers.PaginationMapper;
import com.kiryukhin.mental_health.models.User;
import com.kiryukhin.mental_health.models.meetings.IndividualConsultation;
import com.kiryukhin.mental_health.repositories.meetings.IndividualConsultationRepository;
import com.kiryukhin.mental_health.services.users.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IndividualConsultationServiceImpl implements IndividualConsultationService<IndividualConsultationResponseDto> {

    private final IndividualConsultationRepository individualConsultationRepository;
    private final IndividualConsultationMapper individualConsultationMapper;
    private final UserService userService;
    private final PaginationMapper paginationMapper;


    @Override
    public IndividualConsultationResponseDto getMeeting(UUID id, Authentication authentication) {
        User user = userService.getEntityByAuthenticationOrReturnNull(authentication);

        IndividualConsultation individualConsultation = individualConsultationRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundExceptionCustom(IndividualConsultation.class));

        return individualConsultationMapper.entityToResponseDto(individualConsultation, user);
    }

    @Override
    public PaginationResponseDto<IndividualConsultationResponseDto> getAllMeetings(Pageable pageable, String filter, Authentication authentication) {
        User user = userService.getEntityByAuthenticationOrReturnNull(authentication);

        Page<IndividualConsultation> individualConsultationPage;

        if (filter != null && !filter.isBlank()) {
            individualConsultationPage =  individualConsultationRepository.findAllByIsVisibleTrueAndIsReservedFalseAndByContainingFilters(
                    pageable, filter);
        } else {
            individualConsultationPage = individualConsultationRepository.findAllByIsVisibleTrueAndIsReservedFalse(pageable);
        }

        Page<IndividualConsultationResponseDto> individualConsultationDtoPage = 
                individualConsultationPage.map(ic -> individualConsultationMapper.entityToResponseDto(ic, user));
        return paginationMapper.toPaginationResponseDto(individualConsultationDtoPage);
    }

    @Override
    public PaginationResponseDto<IndividualConsultationResponseDto> searchByStringList(Pageable pageable, List<String> keys, Authentication authentication) {
        User user = userService.getEntityByAuthenticationOrReturnNull(authentication);

        Page<IndividualConsultation> individualConsultationPage =
                individualConsultationRepository.findByKeywordsAndIsVisibleTrueAndIsReservedFalse(pageable, keys);

        Page<IndividualConsultationResponseDto> individualConsultationDtoPage =
                individualConsultationPage.map(ic -> individualConsultationMapper.entityToResponseDto(ic, user));
        return paginationMapper.toPaginationResponseDto(individualConsultationDtoPage);
    }

    @Override
    public List<GroupedMeetingDto<IndividualConsultationResponseDto>> getMeetingsGroupedByDate(
            ZonedDateTime startDate,
            ZonedDateTime endDate,
            Authentication authentication) {
        if (startDate == null) {
            startDate = YearMonth.now().atDay(1).atStartOfDay(ZoneId.systemDefault());
        }
        if (endDate == null) {
            endDate = YearMonth.now().atEndOfMonth().atTime(23, 59, 59).atZone(ZoneId.systemDefault());
        }

        User user = userService.getEntityByAuthenticationOrReturnNull(authentication);

        List<IndividualConsultation> meetings = individualConsultationRepository.findAllByDateTimeStartBetweenAndIsVisibleTrue(startDate, endDate);

        Map<LocalDate, List<IndividualConsultationResponseDto>> groupedByDate = meetings.stream()
                .map(gp -> individualConsultationMapper.entityToResponseDto(gp, user))
                .collect(Collectors.groupingBy(dto -> dto.getDateTimeStart().toLocalDate()));

        return groupedByDate.entrySet().stream()
                .sorted((entry1, entry2) -> entry1.getKey().compareTo(entry2.getKey()))
                .map(entry -> new GroupedMeetingDto<>(entry.getKey(), entry.getValue()))
                .toList();
    }

    @Override
    @Transactional
    public void reserve(UUID id, Authentication authentication) throws BadRequestException {
        User user = userService.getEntityByAuthenticationOrReturnNull(authentication);

        if (user == null) {
            throw new BadRequestException("Invalid authentication or user!");
        }

        for (int retryCount = 0; retryCount < 3; retryCount++) {
            try {
                IndividualConsultation individualConsultation = individualConsultationRepository.findById(id).orElseThrow(() ->
                        new EntityNotFoundExceptionCustom(IndividualConsultation.class));

                if (individualConsultation.isReserved()) {
                    throw new BadRequestException("Consultation is already busy!");
                }

                individualConsultation.setUser(user);
                individualConsultation.setReserved(true);
                individualConsultationRepository.save(individualConsultation);

                return;
            } catch (OptimisticLockingFailureException e) {
                if (retryCount == 2) {
                    throw new ConcurrentModificationException("Unable to reserve consultation after multiple attempts", e);
                }
            }
        }
    }
}
