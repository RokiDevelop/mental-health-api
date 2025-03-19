package com.kiryukhin.mental_health.services.meetings;


import com.kiryukhin.mental_health.dtos.requests.GroupPracticeRequestDto;
import org.apache.coyote.BadRequestException;
import org.springframework.security.core.Authentication;

import java.util.UUID;


public interface GroupPracticeService<ResponseDTO> extends
        MeetingService<GroupPracticeRequestDto, ResponseDTO> {

    void register(UUID id, Authentication authentication) throws BadRequestException;
}
