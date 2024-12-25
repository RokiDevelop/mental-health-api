package com.kiryukhin.mental_health.dtos.responses;

import com.kiryukhin.mental_health.dtos.BaseDto;
import com.kiryukhin.mental_health.dtos.RoleDto;
import com.kiryukhin.mental_health.models.AuthProvider;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserResponseDto extends BaseDto {
    private Long id;

    private String username;

    private String email;

    private AuthProvider authProvider;

    private boolean isBlocked;

    private Set<RoleDto> roles;

    private String firstName;

    private String lastName;

    private LocalDateTime birthday;

    private Boolean isSuperuser;
}