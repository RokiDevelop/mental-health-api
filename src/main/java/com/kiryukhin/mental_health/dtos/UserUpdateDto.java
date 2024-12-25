package com.kiryukhin.mental_health.dtos;


import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserUpdateDto extends BaseDto {
    private String firstName;
    private String lastName;
}