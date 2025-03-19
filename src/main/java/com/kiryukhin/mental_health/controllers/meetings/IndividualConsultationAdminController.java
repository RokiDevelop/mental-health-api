package com.kiryukhin.mental_health.controllers.meetings;

import com.kiryukhin.mental_health.dtos.GroupedMeetingDto;
import com.kiryukhin.mental_health.dtos.requests.IndividualConsultationRequestDto;
import com.kiryukhin.mental_health.dtos.responses.IndividualConsultationAdminResponseDto;
import com.kiryukhin.mental_health.dtos.responses.PaginationResponseDto;
import com.kiryukhin.mental_health.services.meetings.IndividualConsultationServiceAdmin;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/meetings/individual-consultation")
@RequiredArgsConstructor
public class IndividualConsultationAdminController {

    private final IndividualConsultationServiceAdmin<IndividualConsultationAdminResponseDto> individualConsultationServiceAdmin;

    @PostMapping
    public ResponseEntity<IndividualConsultationAdminResponseDto> createIndividualConsultation(
            @Valid @RequestBody IndividualConsultationRequestDto individualConsultationRequestDto) {

        IndividualConsultationAdminResponseDto responseDto = individualConsultationServiceAdmin.createMeeting(
                individualConsultationRequestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<PaginationResponseDto<IndividualConsultationAdminResponseDto>> getAllIndividualConsultations(
            @PageableDefault(size = 10, sort = "dateTimeStart") Pageable pageable,
            @RequestParam(required = false) String filter) {

        PaginationResponseDto<IndividualConsultationAdminResponseDto> responseDto = individualConsultationServiceAdmin.getAllMeetings(pageable, filter);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/grouped-by-date")
    public ResponseEntity<List<GroupedMeetingDto<IndividualConsultationAdminResponseDto>>> getMeetingsGroupedByDate(
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endDate) {

        List<GroupedMeetingDto<IndividualConsultationAdminResponseDto>> groupedMeetings = individualConsultationServiceAdmin.getMeetingsGroupedByDate(startDate, endDate);
        return new ResponseEntity<>(groupedMeetings, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getIndividualConsultation(
            @PathVariable UUID id) {

        IndividualConsultationAdminResponseDto responseDto = individualConsultationServiceAdmin.getMeeting(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IndividualConsultationAdminResponseDto> updateIndividualConsultation(
            @PathVariable UUID id,
            @Valid @RequestBody IndividualConsultationRequestDto individualConsultationRequestDto) {

        IndividualConsultationAdminResponseDto responseDto =
                individualConsultationServiceAdmin.updateMeeting(id, individualConsultationRequestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteIndividualConsultation(
            @PathVariable UUID id) {
        individualConsultationServiceAdmin.deleteMeeting(id);
    }

    @GetMapping("/search")
    public ResponseEntity<PaginationResponseDto<IndividualConsultationAdminResponseDto>> searchIndividualConsultation(
            @PageableDefault(size = 10, sort = "dateTimeStart") Pageable pageable,
            @RequestParam(name = "search") String keywords) {

        List<String> keys = Arrays.stream(keywords.split("([, .])"))
                .map(String::trim)
                .filter(s -> s.matches("\\d*\\p{L}*\\d*"))
                .toList();
        PaginationResponseDto<IndividualConsultationAdminResponseDto> responsePageDto = individualConsultationServiceAdmin.searchByStringList(pageable, keys);
        return new ResponseEntity<>(responsePageDto, HttpStatus.OK);
    }
}
