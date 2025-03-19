package com.kiryukhin.mental_health.services.meetings;

import com.kiryukhin.mental_health.dtos.GroupedMeetingDto;
import com.kiryukhin.mental_health.dtos.responses.PaginationResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public interface MeetingService<RequestDTO, ResponseDTO> {
    ResponseDTO getMeeting(UUID id, Authentication authentication);
    PaginationResponseDto<ResponseDTO> getAllMeetings(Pageable pageable, String filter, Authentication authentication);
    PaginationResponseDto<ResponseDTO> searchByStringList(Pageable pageable, List<String> keys, Authentication authentication);
    List<GroupedMeetingDto<ResponseDTO>> getMeetingsGroupedByDate(ZonedDateTime startDate, ZonedDateTime endDate, Authentication authentication);
}



