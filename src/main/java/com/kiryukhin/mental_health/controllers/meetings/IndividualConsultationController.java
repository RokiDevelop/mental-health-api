package com.kiryukhin.mental_health.controllers.meetings;

import com.kiryukhin.mental_health.dtos.responses.IndividualConsultationResponseDto;
import com.kiryukhin.mental_health.dtos.responses.PaginationResponseDto;
import com.kiryukhin.mental_health.services.meetings.IndividualConsultationService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/meetings/individual-consultation")
@RequiredArgsConstructor
public class IndividualConsultationController {

    private final IndividualConsultationService<IndividualConsultationResponseDto> individualConsultationService;

    @GetMapping
    public ResponseEntity<PaginationResponseDto<IndividualConsultationResponseDto>> getAll(
            @PageableDefault(size = 10, sort = "dateTimeStart") Pageable pageable,
            @RequestParam(required = false) String filter,
            Authentication authentication) {

        PaginationResponseDto<IndividualConsultationResponseDto> responsePageDto =
                individualConsultationService.getAllMeetings(pageable, filter, authentication);
        return new ResponseEntity<>(responsePageDto, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IndividualConsultationResponseDto> getById(
            @PathVariable UUID id,
            Authentication authentication) {

        IndividualConsultationResponseDto responseDto = individualConsultationService.getMeeting(id, authentication);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<PaginationResponseDto<IndividualConsultationResponseDto>> search(
            @PageableDefault(size = 10, sort = "dateTimeStart") Pageable pageable,
            @RequestParam(name = "search") String keywords,
            Authentication authentication) {

        List<String> keys = Arrays.stream(keywords.split("([, .])"))
                .map(String::trim)
                .filter(s -> s.matches("\\d*\\p{L}*\\d*"))
                .toList();

        PaginationResponseDto<IndividualConsultationResponseDto> responsePageDto =
                individualConsultationService.searchByStringList(pageable, keys, authentication);
        return new ResponseEntity<>(responsePageDto, HttpStatus.OK);
    }

    @PostMapping("/{id}/reserve")
    @ResponseStatus(HttpStatus.OK)
    public void reserve(
            @PathVariable UUID id,
            Authentication authentication) throws BadRequestException {

        individualConsultationService.reserve(id, authentication);
    }
}
