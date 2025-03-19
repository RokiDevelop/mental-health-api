package com.kiryukhin.mental_health.services.courses;

import com.kiryukhin.mental_health.dtos.requests.CreateAndUpdateAudioCoursePartsRequestDto;
import com.kiryukhin.mental_health.dtos.responses.AudioCoursePartResponseDto;
import com.kiryukhin.mental_health.dtos.responses.AudioCourseResponseDto;
import com.kiryukhin.mental_health.dtos.responses.PaginationResponseDto;
import com.kiryukhin.mental_health.exeptions.EntityNotFoundExceptionCustom;
import com.kiryukhin.mental_health.exeptions.StorageItemAlreadyExistsException;
import com.kiryukhin.mental_health.mappers.AudioCoursePartMapper;
import com.kiryukhin.mental_health.mappers.PaginationMapper;
import com.kiryukhin.mental_health.models.StorageItem;
import com.kiryukhin.mental_health.models.StorageType;
import com.kiryukhin.mental_health.models.courses.AudioCourse;
import com.kiryukhin.mental_health.models.courses.AudioCoursePart;
import com.kiryukhin.mental_health.repositories.StorageItemRepository;
import com.kiryukhin.mental_health.repositories.courses.AudioCoursePartRepository;
import com.kiryukhin.mental_health.services.StorageItemService;
import com.kiryukhin.mental_health.utils.storages.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Log4j2
public class AudioCoursePartServiceImpl implements AudioCoursePartService {
    private final AudioCoursePartRepository audioCoursePartRepository;
    private final StorageItemRepository storageItemRepository;
    private final StorageServiceFactory storageServiceFactory;
    private final StorageItemService storageItemService;
    private final AudioCourseService<AudioCourseResponseDto> audioCourseService;
    private final AudioCoursePartMapper audioCoursePartMapper;
    private final PaginationMapper paginationMapper;

    @Transactional
    @Override
    public void deleteFileFromPart(Long audioPartId, UUID storageItemId) {
        StorageItem storageItem = storageItemService.getStorageItemById(storageItemId);
        StorageType storageType = storageItem.getStorageType();

        try {
            StorageService storageService = storageServiceFactory.getStorageServiceByStorageTypeEnum(storageType);
            if (storageService instanceof BunnyStreamingStorageService) {
                ((BunnyStreamingStorageService) storageService).deleteAudioStreamFile(storageItem.getStorageObjectId());
            } else if (storageService instanceof AnyFileStorageService) {
                ((AnyFileStorageService) storageService).deleteFile(storageItem.getStorageObjectId());
            }
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("StorageItem type of:{} with audioPartId:{} and storageItemId:{} already not exist", storageType, audioPartId, storageItemId);
        }

        storageItemService.deleteStorageItemById(storageItem.getId());
    }

    @Override
    @Transactional
    public void uploadFileToPart(Long partId, MultipartFile file) {
        BunnyStreamingStorageService bunnyStreamingStorageService = storageServiceFactory.getBunnyStreamingStorageService();
        YandexAnyStorageService yandexAnyStorageService = storageServiceFactory.getYandexStorageService();
        AudioCoursePart audioCoursePart = audioCoursePartRepository.findById(partId)
                .orElseThrow(() -> new EntityNotFoundExceptionCustom(AudioCoursePart.class));

        String fileKey = yandexAnyStorageService.uploadFile(file, "audio_upload_to_bunny");
        String url = yandexAnyStorageService.getFileUrl(fileKey);

        String audioGuid = null;
        Boolean isSuccess = false;
        try {
            String uniqueTitle = audioCoursePart.getTitle() + "_" + UUID.randomUUID();
            audioGuid = bunnyStreamingStorageService.createAudioStreamFile(uniqueTitle);

            String audioFileUrl = bunnyStreamingStorageService.uploadAudioStreamFile(
                    audioGuid,
                    url
            );

            StorageItem storageItem = new StorageItem();
            storageItem.setStorageType(StorageType.BUNNY);
            storageItem.setAudioCoursePart(audioCoursePart);
            storageItem.setUrl(audioFileUrl);
            storageItem.setStorageObjectId(audioGuid);

            for (int retryCount = 0; retryCount < 3; retryCount++) {
                try {
                    if (!audioCoursePart.getStorageItems().contains(storageItem)) {
                        audioCoursePart.getStorageItems().add(storageItem);
                        storageItemRepository.save(storageItem);
                        audioCoursePartRepository.save(audioCoursePart);
                        break;
                    } else {
                        throw new StorageItemAlreadyExistsException("Storage Item exists");
                    }
                } catch (OptimisticLockingFailureException e) {
                    if (retryCount == 2) {
                        throw new ConcurrentModificationException("Failed to add Storage Item after multiple attempts", e);
                    }
                }
            }
            isSuccess = true;

        } catch (Exception e) {
            log.error("Audio upload failed", e);
            throw new RuntimeException("Audio upload failed", e);
        } finally {
            if (!isSuccess && audioGuid != null) {
                try {
                    bunnyStreamingStorageService.deleteAudioStreamFile(audioGuid);
                } catch (RuntimeException ex) {
                    log.error("Failed to delete audio from cloud storage", ex);
                }
            }
            if (isSuccess && !fileKey.isEmpty()) {
                yandexAnyStorageService.deleteFile(fileKey);
            }
        }
    }

    @Override
    @Transactional
    public AudioCoursePart createCoursePart(UUID audioCourseId, CreateAndUpdateAudioCoursePartsRequestDto requestDto) {
        AudioCourse audioCourse = audioCourseService.getAudioCourseEntityById(audioCourseId);
        int nextOrderIndex = getNextOrderIndex(audioCourse);

        AudioCoursePart audioCoursePart = new AudioCoursePart();
        audioCoursePartMapper.updatePartial(audioCoursePart, requestDto);

        audioCoursePart.setAudioCourse(audioCourse);
        audioCoursePart.setOrderIndex(nextOrderIndex);

        audioCoursePartRepository.save(audioCoursePart);
        return audioCoursePart;
    }

    @Override
    @Transactional
    public AudioCoursePartResponseDto createCoursePartAndGetDto(UUID audioCourseId, CreateAndUpdateAudioCoursePartsRequestDto requestDto) {
        AudioCoursePart audioCoursePart = createCoursePart(audioCourseId, requestDto);
        return audioCoursePartMapper.toDto(audioCoursePart);
    }

    @Override
    public AudioCoursePartResponseDto getCoursePartDto(Long id) {
        AudioCoursePart audioCoursePart = audioCoursePartRepository.findById(id)
                .orElseThrow();
        return audioCoursePartMapper.toDto(audioCoursePart);
    }

    @Override
    @Transactional
    public AudioCoursePartResponseDto updateCoursePartAndGetDto(Long id, CreateAndUpdateAudioCoursePartsRequestDto requestDto) {
        AudioCoursePart audioCoursePart = audioCoursePartRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("AudioCoursePart with id {} not found.", id);
                    return new EntityNotFoundExceptionCustom(AudioCoursePart.class);
                });
        audioCoursePartMapper.updatePartial(audioCoursePart, requestDto);
        audioCoursePartRepository.save(audioCoursePart);
        return audioCoursePartMapper.toDto(audioCoursePart);
    }

    @Override
    @Transactional
    public void swapCourseParts(Long partId1, Long partId2) {
        AudioCoursePart part1 = audioCoursePartRepository.findById(partId1)
                .orElseThrow();
        AudioCoursePart part2 = audioCoursePartRepository.findById(partId2)
                .orElseThrow();

        if (!part1.getAudioCourse().equals(part2.getAudioCourse())) {
            throw new IllegalArgumentException("The parts belong to different courses");
        }

        log.info("pre_ordering 1:{}, 2:{}", part1.getOrderIndex(), part2.getOrderIndex());

        audioCoursePartRepository.swapOrderIndexes(
                partId1, part1.getOrderIndex(),
                partId2, part2.getOrderIndex()
        );

        log.info("Swapped: 1:{}, 2:{}", part1.getOrderIndex(), part2.getOrderIndex());
    }

    @Override
    @Transactional
    public void changeOrdersToManyCourseParts(List<Long> partIds, List<Integer> orders) {
        log.info("Updating order indexes for parts: {} with orders: {}", partIds, orders);

        if (partIds.size() != orders.size()) {
            throw new IllegalArgumentException("Number of part IDs must match number of orders.");
        }
        if (new HashSet<>(orders).size() != orders.size()) {
            throw new IllegalArgumentException("Each order must be unique.");
        }

        List<AudioCoursePart> parts = audioCoursePartRepository.findAllById(partIds);
        if (parts.size() != partIds.size()) {
            throw new IllegalArgumentException("All provided part IDs must exist in database.");
        }

        Map<Long, Integer> idToOrderMap = IntStream.range(0, partIds.size())
                .boxed()
                .collect(Collectors.toMap(partIds::get, orders::get));

        parts.forEach(part -> part.setOrderIndex(null));
        audioCoursePartRepository.saveAllAndFlush(parts);

        parts.forEach(part -> part.setOrderIndex(idToOrderMap.get(part.getId())));
        audioCoursePartRepository.saveAll(parts);

        log.info("Successfully updated order indexes for {} parts.", parts.size());
    }

    @Override
    @Transactional
    public void deleteCoursePart(Long partId) {
        AudioCoursePart part = audioCoursePartRepository.findById(partId)
                .orElseThrow();
        AudioCourse audioCourse = part.getAudioCourse();

        BunnyStreamingStorageService bunnyAudioStorageService = storageServiceFactory.getBunnyStreamingStorageService();
        YandexAnyStorageService yandexAnyStorageService = storageServiceFactory.getYandexStorageService();
        LocalAnyStorageService localAnyStorageService = storageServiceFactory.getLocalStorageService();

        int exceptionCount = 0;
        for (StorageItem storageItem : part.getStorageItems()) {
            if (storageItem.getStorageObjectId() != null && !storageItem.getStorageObjectId().isBlank()) {
                if (storageItem.getStorageType() == StorageType.BUNNY) {
                    try {
                        bunnyAudioStorageService.deleteAudioStreamFile(storageItem.getStorageObjectId());
                    } catch (RuntimeException ex) {
                        exceptionCount++;
                        log.error("Failed to delete file from Bunny storage", ex);
                    }
                } else if (storageItem.getStorageType() == StorageType.YANDEX) {
                    try {
                        yandexAnyStorageService.deleteFile(storageItem.getStorageObjectId());
                    } catch (RuntimeException ex) {
                        exceptionCount++;
                        log.error("Failed to delete file from Yandex storage", ex);
                    }
                } else if (storageItem.getStorageType() == StorageType.LOCAL) {
                    try {
                        localAnyStorageService.deleteFile(storageItem.getStorageObjectId());
                    } catch (RuntimeException ex) {
                        exceptionCount++;
                        log.error("Failed to delete file from Local storage", ex);
                    }
                }
            }
        }
        if (exceptionCount == 0) {
            audioCoursePartRepository.delete(part);
            updateOrderIndexes(audioCourse);
        } else {
            throw new RuntimeException("Failed to delete all audio files");
        }
    }

    @Override
    public PaginationResponseDto<AudioCoursePartResponseDto> getPageDtoByCourseId(UUID audioCourseId, Pageable pageable) {
        Page<AudioCoursePart> audioCoursePartPage = audioCoursePartRepository.findAll(pageable);
        Page<AudioCoursePartResponseDto> audioCoursePartResponseDtos = audioCoursePartPage.map(audioCoursePartMapper::toDto);
        return paginationMapper.toPaginationResponseDto(audioCoursePartResponseDtos);
    }

    private void updateOrderIndexes(AudioCourse audioCourse) {
        List<AudioCoursePart> parts = audioCoursePartRepository.findByAudioCourseOrderByOrderIndex(audioCourse);
        for (int i = 0; i < parts.size(); i++) {
            parts.get(i).setOrderIndex(i + 1);
        }
        audioCoursePartRepository.saveAll(parts);
    }

    private int getNextOrderIndex(AudioCourse audioCourse) {
        return audioCoursePartRepository.findMaxOrderIndexByAudioCourse(audioCourse).orElse(0) + 1;
    }
}
