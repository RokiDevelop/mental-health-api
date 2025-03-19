package com.kiryukhin.mental_health.controllers.meetings;

import com.kiryukhin.mental_health.dtos.GroupedMeetingDto;
import com.kiryukhin.mental_health.dtos.requests.GroupPracticeRequestDto;
import com.kiryukhin.mental_health.dtos.responses.GroupPracticeAdminResponseDto;
import com.kiryukhin.mental_health.dtos.responses.PaginationResponseDto;
import com.kiryukhin.mental_health.services.meetings.GroupPracticeServiceAdmin;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/meetings/group-practice")
@RequiredArgsConstructor
public class GroupPracticeAdminController {

    private final GroupPracticeServiceAdmin<GroupPracticeAdminResponseDto> groupPracticeService;

    @PostMapping
    public ResponseEntity<GroupPracticeAdminResponseDto> createWithNotRequiredImage(
            @Valid @ModelAttribute GroupPracticeRequestDto groupPracticeRequestDto,
            @RequestParam(value = "imagePreview", required = false) MultipartFile imagePreview) {

        GroupPracticeAdminResponseDto responseDto = groupPracticeService.createGroupPracticeWithImagePreview(
                    groupPracticeRequestDto, imagePreview);

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<PaginationResponseDto<GroupPracticeAdminResponseDto>> getAll(
            @PageableDefault(size = 10, sort = "dateTimeStart", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String filter) {

        PaginationResponseDto<GroupPracticeAdminResponseDto> responsePageDto =
                groupPracticeService.getAllMeetings(pageable, filter);
        return new ResponseEntity<>(responsePageDto, HttpStatus.OK);
    }

    @GetMapping("/grouped-by-date")
    public ResponseEntity<List<GroupedMeetingDto<GroupPracticeAdminResponseDto>>> getMeetingsGroupedByDate(
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endDate) {

        List<GroupedMeetingDto<GroupPracticeAdminResponseDto>> groupedMeetings = groupPracticeService.getMeetingsGroupedByDate(startDate, endDate);
        return new ResponseEntity<>(groupedMeetings, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupPracticeAdminResponseDto> getGroupPracticeById(
            @PathVariable UUID id) {

        GroupPracticeAdminResponseDto responseDto = groupPracticeService.getMeeting(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GroupPracticeAdminResponseDto> updateGroupPractice(
            @PathVariable UUID id,
            @Valid @RequestBody GroupPracticeRequestDto groupPracticeRequestDto) {

        GroupPracticeAdminResponseDto responseDto = groupPracticeService.updateMeeting(id, groupPracticeRequestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PutMapping("/{id}/upload-preview-image")
    public ResponseEntity<GroupPracticeAdminResponseDto> updatePreviewImageGroupPractice(
            @PathVariable UUID id,
            @RequestParam("imagePreview") MultipartFile imagePreview) throws BadRequestException {

        GroupPracticeAdminResponseDto responseDto = groupPracticeService.updatePreviewImage(id, imagePreview);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGroupPractice(
            @PathVariable UUID id) {

        groupPracticeService.deleteMeeting(id);
    }

    @GetMapping("/search")
    public ResponseEntity<PaginationResponseDto<GroupPracticeAdminResponseDto>> searchGroupPractice(
            @PageableDefault(size = 10, sort = "dateTimeStart") Pageable pageable,
            @RequestParam(name = "search") String keywords) {

        List<String> keys = Arrays.stream(keywords.split("([, .])"))
                .map(String::trim)
                .filter(s -> s.matches("\\d*\\p{L}*\\d*"))
                .toList();
        PaginationResponseDto<GroupPracticeAdminResponseDto> responsePageDto = groupPracticeService.searchByStringList(pageable, keys);
        return new ResponseEntity<>(responsePageDto, HttpStatus.OK);
    }
}
