package com.kiryukhin.mental_health.services.courses;

import com.kiryukhin.mental_health.dtos.requests.CreateAndUpdateVideoCoursePartsRequestDto;
import com.kiryukhin.mental_health.dtos.responses.PaginationResponseDto;
import com.kiryukhin.mental_health.dtos.responses.VideoCoursePartResponseDto;
import com.kiryukhin.mental_health.exeptions.EntityNotFoundExceptionCustom;
import com.kiryukhin.mental_health.exeptions.StorageItemAlreadyExistsException;
import com.kiryukhin.mental_health.mappers.PaginationMapper;
import com.kiryukhin.mental_health.mappers.VideoCoursePartMapper;
import com.kiryukhin.mental_health.models.StorageItem;
import com.kiryukhin.mental_health.models.StorageType;
import com.kiryukhin.mental_health.models.courses.VideoCourse;
import com.kiryukhin.mental_health.models.courses.VideoCoursePart;
import com.kiryukhin.mental_health.repositories.StorageItemRepository;
import com.kiryukhin.mental_health.repositories.courses.VideoCoursePartRepository;
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
public class VideoCoursePartServiceImpl implements VideoCoursePartService {
    private final VideoCoursePartRepository videoCoursePartRepository;
    private final StorageItemRepository storageItemRepository;
    private final StorageServiceFactory storageServiceFactory;
    private final StorageItemService storageItemService;
    private final VideoCourseService<?> videoCourseService;
    private final VideoCoursePartMapper videoCoursePartMapper;
    private final PaginationMapper paginationMapper;

    /***
     * TODO: temporary solution
     * A temporary solution is to perform the deletion directly in the main process.
     * The following flow is recommended:
     *      perform the deletion in a separate service and via a request to Kafka.
     */
    @Transactional
    @Override
    public void deleteFileFromPart(Long videoPartId, UUID storageItemId) {
        StorageItem storageItem = storageItemService.getStorageItemById(storageItemId);
        StorageType storageType = storageItem.getStorageType();

        try {
            StorageService storageService = storageServiceFactory.getStorageServiceByStorageTypeEnum(storageType);
            if (storageService instanceof BunnyStreamingStorageService) {
                ((BunnyStreamingStorageService) storageService).deleteVideoStreamFile(storageItem.getStorageObjectId());
            } else if (storageService instanceof AnyFileStorageService) {
                ((AnyFileStorageService) storageService).deleteFile(storageItem.getStorageObjectId());
            }
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("StorageItem type of:{} with videoPartId:{} and storageItemId:{} already not exist", storageType, videoPartId, storageItemId);
        }

        storageItemService.deleteStorageItemById(storageItem.getId());
    }

    /***
     * TODO: temporary solution
     * A temporary solution is to save to intermediate storage (YCloud) and then save to streaming storage.
     * The following flow is recommended:
     *      save to intermediate storage (YCloud) and then launch the task via Kafka.
     *      And in a separate service, load files from intermediate storage to streaming.
     */
    @Override
    @Transactional
    public void uploadFileToPart(Long partId, MultipartFile file) {
        BunnyStreamingStorageService bunnyStreamingStorageService  = storageServiceFactory.getBunnyStreamingStorageService();
        YandexAnyStorageService yandexAnyStorageService = storageServiceFactory.getYandexStorageService();
        VideoCoursePart videoCoursePart = videoCoursePartRepository.findById(partId)
                .orElseThrow(() -> new EntityNotFoundExceptionCustom(VideoCoursePart.class));

        String fileKey = yandexAnyStorageService.uploadFile(file, "video_upload_to_bunny");
        String url = yandexAnyStorageService.getFileUrl(fileKey);

        String videoGuid = null;
        Boolean isSuccess = false;
        try {

            String uniqueTitle = videoCoursePart.getTitle() + "_" + UUID.randomUUID();
            videoGuid = bunnyStreamingStorageService.createVideoStreamFile(uniqueTitle);

            String videoFileUrl = bunnyStreamingStorageService.uploadVideoFile(
                    videoGuid,
                    url
            );

            StorageItem storageItem = new StorageItem();
            storageItem.setStorageType(StorageType.BUNNY);
            storageItem.setVideoCoursePart(videoCoursePart);
            storageItem.setUrl(videoFileUrl);
            storageItem.setStorageObjectId(videoGuid);

            for (int retryCount = 0; retryCount < 3; retryCount++) {
                try {
                    if (!videoCoursePart.getStorageItems().contains(storageItem)) {
                        videoCoursePart.getStorageItems().add(storageItem);
                        storageItemRepository.save(storageItem);
                        videoCoursePartRepository.save(videoCoursePart);
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
            log.error("Video upload failed", e);
            throw new RuntimeException("Video upload failed", e);
        } finally {
            if (!isSuccess && videoGuid != null) {
                try {
                    bunnyStreamingStorageService.deleteVideoStreamFile(videoGuid);
                } catch (RuntimeException ex) {
                    log.error("Failed to delete video from cloud storage", ex);
                }
            }

            if (isSuccess && !fileKey.isEmpty()) {
                yandexAnyStorageService.deleteFile(fileKey);
            }
        }
    }

    @Override
    @Transactional
    public VideoCoursePart createCoursePart(UUID videoCourseId, CreateAndUpdateVideoCoursePartsRequestDto requestDto) {
        VideoCourse videoCourse = videoCourseService.getVideoCourseEntityById(videoCourseId);

        int nextOrderIndex = getNextOrderIndex(videoCourse);

        VideoCoursePart videoCoursePart = new VideoCoursePart();

        videoCoursePartMapper.updatePartial(videoCoursePart, requestDto);

        videoCoursePart.setVideoCourse(videoCourse);
        videoCoursePart.setOrderIndex(nextOrderIndex);

        videoCoursePartRepository.save(videoCoursePart);
        return videoCoursePart;
    }

    @Override
    @Transactional
    public VideoCoursePartResponseDto createCoursePartAndGetDto(UUID videoCourseId, CreateAndUpdateVideoCoursePartsRequestDto requestDto) {
        VideoCoursePart videoCoursePart = createCoursePart(videoCourseId, requestDto);
        return videoCoursePartMapper.toDto(videoCoursePart);
    }

    @Override
    public VideoCoursePartResponseDto getCoursePartDto(Long id) {
        VideoCoursePart videoCoursePart =
                videoCoursePartRepository.findById(id).orElseThrow();

        return videoCoursePartMapper.toDto(videoCoursePart);
    }

    @Override
    @Transactional
    public VideoCoursePartResponseDto updateCoursePartAndGetDto(Long id, CreateAndUpdateVideoCoursePartsRequestDto requestDto) {
        VideoCoursePart videoCoursePart =
                videoCoursePartRepository.findById(id).orElseThrow(() -> {
                    log.error("VideoCoursePart with id {} not found.", id);
                    return new EntityNotFoundExceptionCustom(VideoCoursePart.class);
                });

        videoCoursePartMapper.updatePartial(videoCoursePart, requestDto);
        videoCoursePartRepository.save(videoCoursePart);

        return videoCoursePartMapper.toDto(videoCoursePart);
    }

    @Override
    @Transactional
    public void swapCourseParts(Long partId1, Long partId2) {
        VideoCoursePart part1 = videoCoursePartRepository.findById(partId1).orElseThrow();
        VideoCoursePart part2 = videoCoursePartRepository.findById(partId2).orElseThrow();

        if (!part1.getVideoCourse().equals(part2.getVideoCourse())) {
            throw new IllegalArgumentException("The parts belong to different courses");
        }

        log.info("pre_ordering 1:{}, 2:{}", part1.getOrderIndex(), part2.getOrderIndex());

        videoCoursePartRepository.swapOrderIndexes(
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

        List<VideoCoursePart> parts = videoCoursePartRepository.findAllById(partIds);
        if (parts.size() != partIds.size()) {
            throw new IllegalArgumentException("All provided part IDs must exist in database.");
        }

        Map<Long, Integer> idToOrderMap = IntStream.range(0, partIds.size())
                .boxed()
                .collect(Collectors.toMap(partIds::get, orders::get));

        parts.forEach(part -> part.setOrderIndex(null));
        videoCoursePartRepository.saveAllAndFlush(parts);

        parts.forEach(part -> part.setOrderIndex(idToOrderMap.get(part.getId())));
        videoCoursePartRepository.saveAll(parts);

        log.info("Successfully updated order indexes for {} parts.", parts.size());
    }


    @Override
    @Transactional
    public void deleteCoursePart(Long partId) {
        VideoCoursePart part = videoCoursePartRepository.findById(partId).orElseThrow();
        VideoCourse videoCourse = part.getVideoCourse();

        BunnyStreamingStorageService bunnyStreamService = storageServiceFactory.getBunnyStreamingStorageService();
        YandexAnyStorageService yandexAnyStorageService = storageServiceFactory.getYandexStorageService();
        LocalAnyStorageService localAnyStorageService = storageServiceFactory.getLocalStorageService();

        int exceptionCount = 0;

        for (StorageItem storageItem : part.getStorageItems()) {
            if (storageItem.getStorageObjectId() != null && !storageItem.getStorageObjectId().isBlank()) {
                if (storageItem.getStorageType() == StorageType.BUNNY) {
                    try {
                        bunnyStreamService.deleteVideoStreamFile(storageItem.getStorageObjectId());
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
            videoCoursePartRepository.delete(part);
            updateOrderIndexes(videoCourse);
        } else {
            throw new RuntimeException("");
        }

    }

    @Override
    public PaginationResponseDto<VideoCoursePartResponseDto> getPageDtoByCourseId(UUID videoCourseId, Pageable pageable) {
        Page<VideoCoursePart> videoCoursePartPage = videoCoursePartRepository.findAll(pageable);
        Page<VideoCoursePartResponseDto> videoCoursePartResponseDtos = videoCoursePartPage.map(videoCoursePartMapper::toDto);
        return paginationMapper.toPaginationResponseDto(videoCoursePartResponseDtos);
    }

    private void updateOrderIndexes(VideoCourse videoCourse) {
        List<VideoCoursePart> parts = videoCoursePartRepository.findByVideoCourseOrderByOrderIndex(videoCourse);
        for (int i = 0; i < parts.size(); i++) {
            parts.get(i).setOrderIndex(i + 1);
        }
        videoCoursePartRepository.saveAll(parts);
    }

    private int getNextOrderIndex(VideoCourse videoCourse) {
        return videoCoursePartRepository.findMaxOrderIndexByVideoCourse(videoCourse).orElse(0) + 1;
    }
}