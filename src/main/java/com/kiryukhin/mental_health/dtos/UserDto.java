package com.kiryukhin.mental_health.dtos;

import com.kiryukhin.mental_health.models.AuthProvider;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserDto extends BaseDto {
    private Long id;

    private String username;

    private String email;

    private String password;

    private AuthProvider authProvider;

    private boolean isBlocked;

    private boolean isVerified;

    private Set<RoleDto> roles;

    private String firstName;

    private String lastName;

    private LocalDateTime birthday;

    private Boolean isSuperuser;
}