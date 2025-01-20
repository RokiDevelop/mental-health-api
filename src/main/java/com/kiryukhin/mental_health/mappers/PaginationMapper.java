package com.kiryukhin.mental_health.mappers;

import com.kiryukhin.mental_health.dtos.responses.PaginationResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class PaginationMapper {

    public <T> PaginationResponseDto<T> toPaginationResponseDto(Page<T> page) {
        return new PaginationResponseDto<>(
                page.getTotalPages(),
                page.getTotalElements(),
                page.getNumber(),
                page.getSize(),
                page.getContent()
        );
    }
}

