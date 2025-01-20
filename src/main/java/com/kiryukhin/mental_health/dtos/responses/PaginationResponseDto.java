package com.kiryukhin.mental_health.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginationResponseDto<T>{
    private int totalPages;
    private long totalElements;
    private int pageNumber;
    private int pageSize;
    private List<T> content;

    public PaginationResponseDto(Page<T> page) {
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.pageNumber = page.getNumber();
        this.pageSize = page.getSize();
        this.content = page.getContent();
    }
}
