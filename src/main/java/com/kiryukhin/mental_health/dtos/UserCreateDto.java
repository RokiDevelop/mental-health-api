package com.kiryukhin.mental_health.dtos;

import com.kiryukhin.mental_health.models.AuthProvider;
import com.kiryukhin.mental_health.validations.UniqueUsername;
import com.kiryukhin.mental_health.validations.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserCreateDto extends BaseDto {
    @NotNull
    @UniqueUsername
    private String username;

    @NotNull
    @Email
    private String email;

    @NotNull
    @ValidPassword
    private String password;

    private AuthProvider authProvider;

    private Set<RoleDto> roles;

    private String firstName;

    private String lastName;

    private LocalDateTime birthday;
}