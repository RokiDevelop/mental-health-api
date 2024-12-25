package com.kiryukhin.mental_health.security;

import com.kiryukhin.mental_health.models.AuthProvider;
import com.kiryukhin.mental_health.models.User;
import com.kiryukhin.mental_health.repositories.UserRepository;
import com.kiryukhin.mental_health.servicesLogic.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userService.getUserForUserDetails(username);

        return new CustomUserDetails(user);
    }

    @Transactional
    public void updateAuthProvider(String username, String authProviderName) {
        AuthProvider authType = AuthProvider.valueOf(authProviderName.toUpperCase());
        userRepository.updateAuthProvider(username, authType);
    }
}