package com.kiryukhin.mental_health.dtos.request;

import com.kiryukhin.mental_health.dtos.UserDto;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
public class RegistrationRequest {
    @NotBlank private String name;
    @NotBlank private String email;
    @NotBlank private String username;
    @NotBlank private String password;
    @NotBlank private String confirmPassword;
    @NotBlank private String firstName;
    @NotBlank private String lastName;
    private LocalDateTime birthday;

}