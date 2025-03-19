package com.kiryukhin.mental_health.services.meetings;


import com.kiryukhin.mental_health.dtos.requests.IndividualConsultationRequestDto;
import org.apache.coyote.BadRequestException;
import org.springframework.security.core.Authentication;

import java.util.UUID;

public interface IndividualConsultationService<ResponseDTO> extends
        MeetingService<IndividualConsultationRequestDto, ResponseDTO> {
    void reserve(UUID id, Authentication authentication) throws BadRequestException;
}
