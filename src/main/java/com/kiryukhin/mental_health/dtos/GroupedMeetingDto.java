package com.kiryukhin.mental_health.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupedMeetingDto<T> {
    private LocalDate date;

    private List<T> meetings;
}