package com.kiryukhin.mental_health.controllers;

import com.kiryukhin.mental_health.dtos.UserUpdateDto;
import com.kiryukhin.mental_health.dtos.responses.UserResponseDto;
import com.kiryukhin.mental_health.services.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/user-profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userService;

    @GetMapping(value = "/")
    public ResponseEntity<?> getUserProfile(Authentication authentication) {
        String username = authentication.getName();
        UserResponseDto userInfo = userService.getByUsername(username);

        return ResponseEntity.ok().body(userInfo);
    }

    @PutMapping(value = "/")
    public ResponseEntity<?> updateUserProfile(@RequestBody UserUpdateDto userUpdateDto,
                                               Authentication authentication) {
        String username = authentication.getName();

        UserResponseDto user = userService.updateUserByUsername(username, userUpdateDto);

        return ResponseEntity.ok().body(user);
    }
}
