package com.kiryukhin.mental_health.services.meetings;

import com.kiryukhin.mental_health.dtos.GroupedMeetingDto;
import com.kiryukhin.mental_health.dtos.responses.GroupPracticeResponseDto;
import com.kiryukhin.mental_health.dtos.responses.PaginationResponseDto;
import com.kiryukhin.mental_health.exeptions.EntityNotFoundExceptionCustom;
import com.kiryukhin.mental_health.exeptions.UserAlreadyAssignedException;
import com.kiryukhin.mental_health.mappers.GroupPracticeMapper;
import com.kiryukhin.mental_health.mappers.PaginationMapper;
import com.kiryukhin.mental_health.models.User;
import com.kiryukhin.mental_health.models.meetings.GroupPractice;
import com.kiryukhin.mental_health.repositories.meetings.GroupPracticeRepository;
import com.kiryukhin.mental_health.services.users.UserService;
import com.kiryukhin.mental_health.utils.storages.AnyFileStorageService;
import com.kiryukhin.mental_health.utils.storages.StorageServiceFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupPracticeServiceImpl implements GroupPracticeService<GroupPracticeResponseDto> {

    private final GroupPracticeRepository groupPracticeRepository;
    private final GroupPracticeMapper groupPracticeMapper;
    private final StorageServiceFactory storageServiceFactory;
    private final UserService userService;
    private final PaginationMapper paginationMapper;


    @Override
    public GroupPracticeResponseDto getMeeting(UUID id, Authentication authentication) {
        User user = userService.getEntityByAuthenticationOrReturnNull(authentication);

        GroupPractice groupPractice = groupPracticeRepository.findByIdAndIsVisibleTrue(id).orElseThrow(() ->
                new EntityNotFoundExceptionCustom(GroupPractice.class));

        return getGroupPracticeResponseDtoWithFullImagePreviewUrl(groupPractice, user);
    }

    @Override
    public PaginationResponseDto<GroupPracticeResponseDto> getAllMeetings(Pageable pageable, String filter, Authentication authentication) {
        User user = userService.getEntityByAuthenticationOrReturnNull(authentication);

        Page<GroupPractice> groupPracticePage;

        if (filter != null && !filter.isBlank()) {
            groupPracticePage = groupPracticeRepository.findAllByIsVisibleTrueAndByContainingFilters(
                    pageable, filter);
        } else {
            groupPracticePage = groupPracticeRepository.findAllByIsVisibleTrue(pageable);
        }

        Page<GroupPracticeResponseDto> groupPracticeDtoPage =
                groupPracticePage.map(gp -> getGroupPracticeResponseDtoWithFullImagePreviewUrl(gp, user));

        return paginationMapper.toPaginationResponseDto(groupPracticeDtoPage);
    }

    @Override
    public PaginationResponseDto<GroupPracticeResponseDto> searchByStringList(Pageable pageable, List<String> keys, Authentication authentication) {
        User user = userService.getEntityByAuthenticationOrReturnNull(authentication);

        Page<GroupPractice> groupPracticePage = groupPracticeRepository.findByKeywordsAndIsVisibleTrue(pageable, keys);

        Page<GroupPracticeResponseDto> groupPracticeDtoPage =
                groupPracticePage.map(gp -> getGroupPracticeResponseDtoWithFullImagePreviewUrl(gp, user));

        return paginationMapper.toPaginationResponseDto(groupPracticeDtoPage);
    }

    @Override
    public List<GroupedMeetingDto<GroupPracticeResponseDto>> getMeetingsGroupedByDate(
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

        List<GroupPractice> meetings = groupPracticeRepository.findAllByDateTimeStartBetweenAndIsVisibleTrue(startDate, endDate);

        Map<LocalDate, List<GroupPracticeResponseDto>> groupedByDate = meetings.stream()
                .map(gp -> getGroupPracticeResponseDtoWithFullImagePreviewUrl(gp, user))
                .collect(Collectors.groupingBy(dto -> dto.getDateTimeStart().toLocalDate()));

        return groupedByDate.entrySet().stream()
                .sorted((entry1, entry2) -> entry1.getKey().compareTo(entry2.getKey()))
                .map(entry -> new GroupedMeetingDto<>(entry.getKey(), entry.getValue()))
                .toList();
    }

    @Override
    @Transactional
    public void register(UUID id, Authentication authentication) throws BadRequestException {
        User user = userService.getEntityByAuthenticationOrReturnNull(authentication);

        if (user == null) {
            throw new BadRequestException("Invalid authentication or user!");
        }

        for (int retryCount = 0; retryCount < 3; retryCount++) {
            try {
                GroupPractice groupPractice = groupPracticeRepository.findByIdAndIsVisibleTrueAndIsFinishedFalse(id)
                        .orElseThrow(() -> new EntityNotFoundExceptionCustom(GroupPractice.class));

                if (!groupPractice.getUsers().contains(user)) {
                    groupPractice.getUsers().add(user);
                    groupPracticeRepository.save(groupPractice);
                } else {
                    throw new UserAlreadyAssignedException("User Already Assigned");
                }

                return;
            } catch (OptimisticLockingFailureException e) {
                if (retryCount == 2) {
                    throw new ConcurrentModificationException("Unable to register user after multiple attempts", e);
                }
            }
        }
    }


    private GroupPracticeResponseDto getGroupPracticeResponseDtoWithFullImagePreviewUrl(GroupPractice groupPractice, User currentUser) {
        AnyFileStorageService storageService = storageServiceFactory.getStorageService();
        GroupPracticeResponseDto dto = groupPracticeMapper.entityToResponseDto(groupPractice, currentUser);

        if (groupPractice.getImagePreviewUrl() != null) {
            String fullImagePreviewUrl = storageService.getFileUrl(groupPractice.getImagePreviewUrl());
            dto.setImagePreviewUrl(fullImagePreviewUrl);
        }

        return dto;
    }
}
