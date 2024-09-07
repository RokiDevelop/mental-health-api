package com.kiryukhin.mental_health.security;

import com.kiryukhin.mental_health.models.OAuth2Provider;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Data
public class CustomOauthUserDetails implements OAuth2User {
    private Long id;
    private String username;
    private String name;
    private String email;
    private String avatarUrl;
    private OAuth2Provider provider;
    private Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes;
}