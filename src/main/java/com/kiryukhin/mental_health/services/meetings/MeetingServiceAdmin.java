package com.kiryukhin.mental_health.services.meetings;
import com.kiryukhin.mental_health.dtos.GroupedMeetingDto;
import com.kiryukhin.mental_health.dtos.responses.PaginationResponseDto;
import org.springframework.data.domain.Pageable;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public interface MeetingServiceAdmin<RequestDTO, ResponseDTO> {
    ResponseDTO createMeeting(RequestDTO dto);

    ResponseDTO getMeeting(UUID id);

    PaginationResponseDto<ResponseDTO> searchByStringList(Pageable pageable, List<String> keys);
    List<GroupedMeetingDto<ResponseDTO>> getMeetingsGroupedByDate(ZonedDateTime startDate, ZonedDateTime endDate);
    PaginationResponseDto<ResponseDTO> getAllMeetings(Pageable pageable, String filter);

    ResponseDTO updateMeeting(UUID id, RequestDTO dto);

    void deleteMeeting(UUID id);
}
