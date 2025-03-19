package com.kiryukhin.mental_health.services.meetings;

import com.kiryukhin.mental_health.dtos.GroupedMeetingDto;
import com.kiryukhin.mental_health.dtos.requests.GroupPracticeRequestDto;
import com.kiryukhin.mental_health.dtos.responses.GroupPracticeAdminResponseDto;
import com.kiryukhin.mental_health.dtos.responses.PaginationResponseDto;
import com.kiryukhin.mental_health.exeptions.EntityNotFoundExceptionCustom;
import com.kiryukhin.mental_health.mappers.GroupPracticeMapper;
import com.kiryukhin.mental_health.mappers.PaginationMapper;
import com.kiryukhin.mental_health.models.meetings.GroupPractice;
import com.kiryukhin.mental_health.repositories.meetings.GroupPracticeRepository;
import com.kiryukhin.mental_health.utils.storages.AnyFileStorageService;
import com.kiryukhin.mental_health.utils.storages.StorageServiceFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
@Log4j2
public class GroupPracticeServiceAdminImpl implements GroupPracticeServiceAdmin<GroupPracticeAdminResponseDto> {

    private final GroupPracticeRepository groupPracticeRepository;
    private final GroupPracticeMapper groupPracticeMapper;
    private final StorageServiceFactory storageServiceFactory;
    private final PaginationMapper paginationMapper;


    @Override
    public GroupPracticeAdminResponseDto getMeeting(UUID id) {
        Optional<GroupPractice> optionalGroupPractice = groupPracticeRepository.findById(id);
        GroupPractice groupPractice = optionalGroupPractice.orElseThrow(() ->
                new EntityNotFoundExceptionCustom(GroupPractice.class));
        ;

        return getGroupPracticeAdminResponseDtoWithFullImagePreviewUrl(groupPractice);
    }

    @Override
    public PaginationResponseDto<GroupPracticeAdminResponseDto> getAllMeetings(Pageable pageable, String filter) {
        Page<GroupPractice> groupPracticePage;

        if (filter != null && !filter.isBlank()) {
            groupPracticePage = groupPracticeRepository.findAllByDetailsContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrTitleContainingIgnoreCase(
                    pageable, filter, filter, filter);
        } else {
            groupPracticePage = groupPracticeRepository.findAll(pageable);
        }

        Page<GroupPracticeAdminResponseDto> groupPracticeDtoPage = groupPracticePage
                .map(this::getGroupPracticeAdminResponseDtoWithFullImagePreviewUrl);

        return paginationMapper.toPaginationResponseDto(groupPracticeDtoPage);
    }

    @Override
    public GroupPracticeAdminResponseDto createMeeting(GroupPracticeRequestDto groupPracticeRequestDto) {
        GroupPractice groupPractice = groupPracticeMapper.requestDtoToEntity(groupPracticeRequestDto);
        groupPractice = groupPracticeRepository.save(groupPractice);

        return getGroupPracticeAdminResponseDtoWithFullImagePreviewUrl(groupPractice);
    }

    @Override
    public GroupPracticeAdminResponseDto createGroupPracticeWithImagePreview(GroupPracticeRequestDto groupPracticeRequestDto, MultipartFile imagePreview) {
        GroupPractice groupPractice = groupPracticeMapper.requestDtoToEntity(groupPracticeRequestDto);

        if (imagePreview != null && !imagePreview.isEmpty()) {
            AnyFileStorageService storageService = storageServiceFactory.getStorageService();
            String key = storageService.uploadFile(imagePreview, "meetings/group_practice");
            groupPractice.setImagePreviewUrl(key);
        }

        groupPractice = groupPracticeRepository.save(groupPractice);

        return getGroupPracticeAdminResponseDtoWithFullImagePreviewUrl(groupPractice);
    }

    @Override
    public void deleteMeeting(UUID id) {
        Optional<GroupPractice> optionalGroupPractice = groupPracticeRepository.findById(id);
        GroupPractice groupPractice = optionalGroupPractice.orElseThrow(() ->
                new EntityNotFoundExceptionCustom(GroupPractice.class));

        if (groupPractice.getImagePreviewUrl() != null && !groupPractice.getImagePreviewUrl().isBlank()) {
            try {
                AnyFileStorageService storageService = storageServiceFactory.getStorageService();
                storageService.deleteFile(groupPractice.getImagePreviewUrl());
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }

        groupPracticeRepository.delete(groupPractice);
    }

    @Override
    public GroupPracticeAdminResponseDto updateMeeting(UUID id, GroupPracticeRequestDto groupPracticeRequestDto) {
        Optional<GroupPractice> optionalGroupPractice = groupPracticeRepository.findById(id);
        GroupPractice groupPractice = optionalGroupPractice.orElseThrow(() ->
                new EntityNotFoundExceptionCustom(GroupPractice.class));
        groupPracticeMapper.updatePartialFromGroupPracticeRequestDto(groupPractice, groupPracticeRequestDto);
        groupPracticeRepository.save(groupPractice);

        return getGroupPracticeAdminResponseDtoWithFullImagePreviewUrl(groupPractice);
    }

    @Override
    public GroupPracticeAdminResponseDto updatePreviewImage(UUID id, MultipartFile imagePreview) throws BadRequestException {
        if (imagePreview == null || imagePreview.isEmpty()) {
            throw new BadRequestException("Image file is empty!");
        }
        Optional<GroupPractice> optionalGroupPractice = groupPracticeRepository.findById(id);
        GroupPractice groupPractice = optionalGroupPractice.orElseThrow(() ->
                new EntityNotFoundExceptionCustom(GroupPractice.class));

        AnyFileStorageService storageService = storageServiceFactory.getStorageService();
        String key;
        if (groupPractice.getImagePreviewUrl() != null && !groupPractice.getImagePreviewUrl().isBlank()) {
            try {
                key = storageService.updateFile(imagePreview, groupPractice.getImagePreviewUrl());
            } catch (RuntimeException e) {
                key = storageService.uploadFile(imagePreview, "meetings/group_practice");
            }
        } else {
            key = storageService.uploadFile(imagePreview, "meetings/group_practice");
        }

        groupPractice.setImagePreviewUrl(key);
        groupPracticeRepository.save(groupPractice);

        return getGroupPracticeAdminResponseDtoWithFullImagePreviewUrl(groupPractice);
    }

    @Override
    public PaginationResponseDto<GroupPracticeAdminResponseDto> searchByStringList(Pageable pageable, List<String> keys) {
        Page<GroupPractice> groupPracticePage = groupPracticeRepository.findByKeywords(pageable, keys);

        Page<GroupPracticeAdminResponseDto> groupPracticeDtoPage = groupPracticePage
                .map(this::getGroupPracticeAdminResponseDtoWithFullImagePreviewUrl);

        return paginationMapper.toPaginationResponseDto(groupPracticeDtoPage);
    }

    @Override
    public List<GroupedMeetingDto<GroupPracticeAdminResponseDto>> getMeetingsGroupedByDate(ZonedDateTime startDate, ZonedDateTime endDate) {
        if (startDate == null) {
            startDate = YearMonth.now().atDay(1).atStartOfDay(ZoneId.systemDefault());
        }
        if (endDate == null) {
            endDate = YearMonth.now().atEndOfMonth().atTime(23, 59, 59).atZone(ZoneId.systemDefault());
        }

        List<GroupPractice> meetings = groupPracticeRepository.findAllByDateTimeStartBetween(startDate, endDate);

        Map<LocalDate, List<GroupPracticeAdminResponseDto>> groupedByDate = meetings.stream()
                .map(this::getGroupPracticeAdminResponseDtoWithFullImagePreviewUrl)
                .collect(Collectors.groupingBy(dto -> dto.getDateTimeStart().toLocalDate()));

        return groupedByDate.entrySet().stream()
                .sorted((entry1, entry2) -> entry1.getKey().compareTo(entry2.getKey()))
                .map(entry -> new GroupedMeetingDto<>(entry.getKey(), entry.getValue()))
                .toList();
    }

    private GroupPracticeAdminResponseDto getGroupPracticeAdminResponseDtoWithFullImagePreviewUrl(GroupPractice groupPractice) {
        AnyFileStorageService storageService = storageServiceFactory.getStorageService();
        GroupPracticeAdminResponseDto dto = groupPracticeMapper.entityToAdminResponseDto(groupPractice);

        if (groupPractice.getImagePreviewUrl() != null) {
            String fullImagePreviewUrl = storageService.getFileUrl(groupPractice.getImagePreviewUrl());
            dto.setImagePreviewUrl(fullImagePreviewUrl);
        }

        return dto;
    }
}
