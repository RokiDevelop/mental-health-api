package com.kiryukhin.mental_health.security;

import com.kiryukhin.mental_health.exeptions.UserIsBlockedException;
import com.kiryukhin.mental_health.models.AuthProvider;
import com.kiryukhin.mental_health.models.User;
import com.kiryukhin.mental_health.repositories.UserRepository;
import com.kiryukhin.mental_health.servicesMapping.UserMappingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMappingService userService;
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.getUserForUserDetails(username);
        if (user.isBlocked()) {
            throw new UserIsBlockedException("user is disabled");
        }

        return new CustomUserDetails(user);
    }

    @Transactional
    public void updateAuthProvider(String username, String authProviderName) {
        AuthProvider authType = AuthProvider.valueOf(authProviderName.toUpperCase());
        userRepository.updateAuthProvider(username, authType);
    }
}