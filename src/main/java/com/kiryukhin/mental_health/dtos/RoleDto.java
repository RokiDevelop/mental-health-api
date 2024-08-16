package com.kiryukhin.mental_health.dtos;

import com.kiryukhin.mental_health.validations.CreateValidationGroup;
import com.kiryukhin.mental_health.validations.UpdateValidationGroup;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleDto extends BaseDto {

    @Null(groups = {CreateValidationGroup.class})
    @NotNull(groups = {UpdateValidationGroup.class})
    private Long id;

    private String name;
}