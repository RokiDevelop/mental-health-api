package com.kiryukhin.mental_health.dtos;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto extends BaseDto {
    private String firstName;
    private String lastName;
}