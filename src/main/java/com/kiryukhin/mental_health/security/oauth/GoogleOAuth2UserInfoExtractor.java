package com.kiryukhin.mental_health.security.oauth;

import com.kiryukhin.mental_health.dtos.responses.UserResponseDto;
import com.kiryukhin.mental_health.models.AuthProvider;
import com.kiryukhin.mental_health.security.CustomOauthUserDetails;
import com.kiryukhin.mental_health.services.users.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class GoogleOAuth2UserInfoExtractor implements OAuth2UserInfoExtractor {

    private final UserService userService;

    @Override
    public CustomOauthUserDetails extractUserInfo(OAuth2User oAuth2User) {
        CustomOauthUserDetails customUserDetails = new CustomOauthUserDetails();
        String email = retrieveAttr("email", oAuth2User);

        try {
            UserResponseDto exist_user = userService.getByEmail(email);
            customUserDetails.setUsername(exist_user.getUsername());
        } catch (UsernameNotFoundException e) {
            String username = generateUniqueUsername(oAuth2User);
            customUserDetails.setUsername(username);
        }

        customUserDetails.setEmail(email);
        customUserDetails.setName(retrieveAttr("name", oAuth2User));
        customUserDetails.setAvatarUrl(retrieveAttr("picture", oAuth2User));
        customUserDetails.setFirstName(retrieveAttr("given_name", oAuth2User));
        customUserDetails.setLastName(retrieveAttr("family_name", oAuth2User));
        customUserDetails.setProvider(AuthProvider.GOOGLE);
        customUserDetails.setAttributes(oAuth2User.getAttributes());
        return customUserDetails;
    }

    @Override
    public CustomOauthUserDetails extractUserInfo(OAuth2User oAuth2User, OAuth2UserRequest oAuth2UserRequest) {
        return this.extractUserInfo(oAuth2User);
    }

    @Override
    public boolean accepts(OAuth2UserRequest userRequest) {
        return AuthProvider.GOOGLE
                .name()
                .equalsIgnoreCase(userRequest.getClientRegistration().getRegistrationId());
    }

    private String retrieveAttr(String attr, OAuth2User oAuth2User) {
        Object attribute = oAuth2User.getAttributes().get(attr);
        return attribute == null ? "" : attribute.toString();
    }

    private String generateUniqueUsername(OAuth2User oAuth2User) {
        String username_base = retrieveAttr("email", oAuth2User).split("@")[0];
        Set<String> exist_usernames = userService.findUsernameSetByUsername(username_base);

        String sub = retrieveAttr("sub", oAuth2User);
        String username_suffix;

        byte i = 0;
        do {
            if (i <= sub.length()) {
                username_suffix = sub.substring(0, i);
                i++;
            } else {
                username_suffix = RandomStringUtils.randomAlphanumeric(6, 8);
            }
        } while (exist_usernames.contains(username_base + username_suffix));
        return username_base + username_suffix;
    }
}
