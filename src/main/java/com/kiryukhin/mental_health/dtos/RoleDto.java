package com.kiryukhin.mental_health.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleDto {
    private Long id;

    @NotBlank
    private String name;
}
