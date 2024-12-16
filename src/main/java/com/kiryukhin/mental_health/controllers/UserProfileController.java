package com.kiryukhin.mental_health.controllers;

import com.kiryukhin.mental_health.dtos.UserUpdateDto;
import com.kiryukhin.mental_health.servicesMapping.UserMappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/user-profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserMappingService userMappingService;

    @GetMapping(value = "/")
    public ResponseEntity<?> getUserProfile(Authentication authentication) {
        String username = authentication.getName();
        var userInfo = userMappingService.getByUsername(username);

        return ResponseEntity.ok().body(userInfo);
    }

    @PutMapping(value = "/")
    public ResponseEntity<?> updateUserProfile(@RequestBody UserUpdateDto userDto,
                                               Authentication authentication) {
        String username = authentication.getName();

        var user = userMappingService.updateUserByUsername(username, userDto);

        return ResponseEntity.ok().body(user);
    }
}
