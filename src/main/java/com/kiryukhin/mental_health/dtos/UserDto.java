package com.kiryukhin.mental_health.dtos;

import com.kiryukhin.mental_health.validations.CreateValidationGroup;
import com.kiryukhin.mental_health.validations.UpdateValidationGroup;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto extends BaseDto {

    @Null(groups = {CreateValidationGroup.class})
    @NotNull(groups = {UpdateValidationGroup.class})
    private Long id;

    private String name;

    private String username;

    @NotNull(groups = {CreateValidationGroup.class})
    @Null(groups = {UpdateValidationGroup.class})
    private String password;

    private boolean isEnabled;

    private Set<RoleDto> roles;
}