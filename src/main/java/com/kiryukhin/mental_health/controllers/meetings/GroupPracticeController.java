package com.kiryukhin.mental_health.controllers.meetings;

import com.kiryukhin.mental_health.dtos.GroupedMeetingDto;
import com.kiryukhin.mental_health.dtos.responses.GroupPracticeResponseDto;
import com.kiryukhin.mental_health.dtos.responses.PaginationResponseDto;
import com.kiryukhin.mental_health.services.meetings.GroupPracticeService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/meetings/group-practice")
@RequiredArgsConstructor
public class GroupPracticeController {

    private final GroupPracticeService<GroupPracticeResponseDto> groupPracticeService;


    @GetMapping
    public ResponseEntity<PaginationResponseDto<GroupPracticeResponseDto>> getAllGroupPractices(
            @PageableDefault(size = 10, sort = "dateTimeStart") Pageable pageable,
            @RequestParam(required = false) String filter,
            Authentication authentication) {

        PaginationResponseDto<GroupPracticeResponseDto> responsePageDto = groupPracticeService.getAllMeetings(pageable, filter, authentication);
        return new ResponseEntity<>(responsePageDto, HttpStatus.OK);
    }

    @GetMapping("/grouped-by-date")
    public ResponseEntity<List<GroupedMeetingDto<GroupPracticeResponseDto>>> getMeetingsGroupedByDate(
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endDate,
            Authentication authentication) {

        List<GroupedMeetingDto<GroupPracticeResponseDto>> groupedMeetings =
                groupPracticeService.getMeetingsGroupedByDate(startDate, endDate, authentication);
        return new ResponseEntity<>(groupedMeetings, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupPracticeResponseDto> getGroupPractice(
            @PathVariable UUID id,
            Authentication authentication) {

        GroupPracticeResponseDto responseDto = groupPracticeService.getMeeting(id, authentication);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<PaginationResponseDto<GroupPracticeResponseDto>> searchGroupPractice(
            @PageableDefault(size = 10, sort = "dateTimeStart") Pageable pageable,
            @RequestParam(name = "search") String keywords,
            Authentication authentication) {

        List<String> keys = Arrays.stream(keywords.split("([, .])"))
                .map(String::trim)
                .filter(s -> s.matches("\\d*\\p{L}*\\d*"))
                .toList();
        PaginationResponseDto<GroupPracticeResponseDto> responsePageDto = groupPracticeService.searchByStringList(pageable, keys, authentication);
        return new ResponseEntity<>(responsePageDto, HttpStatus.OK);
    }

    @PostMapping("/{id}/register")
    @ResponseStatus(HttpStatus.OK)
    public void registerToGroupPractice(
            @PathVariable UUID id,
            Authentication authentication) throws BadRequestException {

        groupPracticeService.register(id, authentication);
    }
}
