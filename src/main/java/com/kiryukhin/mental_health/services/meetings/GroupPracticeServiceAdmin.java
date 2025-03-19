package com.kiryukhin.mental_health.services.meetings;


import com.kiryukhin.mental_health.dtos.requests.GroupPracticeRequestDto;
import org.apache.coyote.BadRequestException;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface GroupPracticeServiceAdmin<ResponseDTO> extends
        MeetingServiceAdmin<GroupPracticeRequestDto, ResponseDTO>{
    ResponseDTO createGroupPracticeWithImagePreview(GroupPracticeRequestDto requestDto, MultipartFile imagePreview);
    ResponseDTO updatePreviewImage(UUID id, MultipartFile imagePreview) throws BadRequestException;
}
