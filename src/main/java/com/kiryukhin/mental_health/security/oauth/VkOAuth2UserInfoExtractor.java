package com.kiryukhin.mental_health.security.oauth;

import com.kiryukhin.mental_health.dtos.responses.UserResponseDto;
import com.kiryukhin.mental_health.models.AuthProvider;
import com.kiryukhin.mental_health.security.CustomOauthUserDetails;
import com.kiryukhin.mental_health.servicesLogic.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class VkOAuth2UserInfoExtractor implements OAuth2UserInfoExtractor {

    private final UserService userService;

    @Override
    public CustomOauthUserDetails extractUserInfo(OAuth2User oAuth2User) {
        CustomOauthUserDetails customUserDetails = new CustomOauthUserDetails();
        List<Map<String, Object>> response =
                (List<Map<String, Object>>) oAuth2User
                        .getAttributes()
                        .getOrDefault("response", List.of());
        customUserDetails.setFirstName(retrieveAttr("first_name", response));
        customUserDetails.setLastName(retrieveAttr("last_name", response));
        customUserDetails.setName(
                retrieveAttr("first_name", response)
                        .concat(" ")
                        .concat(retrieveAttr("last_name", response)));
        customUserDetails.setAvatarUrl(retrieveAttr("photo_max", response));
        customUserDetails.setProvider(AuthProvider.VK);
        customUserDetails.setAttributes(response.get(0));
        return customUserDetails;
    }

    @Override
    public CustomOauthUserDetails extractUserInfo(OAuth2User oAuth2User, OAuth2UserRequest oAuth2UserRequest) {
        CustomOauthUserDetails customUserDetails = this.extractUserInfo(oAuth2User);
        Map<String, Object> userRequestAttrs = oAuth2UserRequest.getAdditionalParameters();

        String email = (String) userRequestAttrs.get("email");
        String user_id = Integer.toString((Integer) userRequestAttrs.get("user_id"));

        UserResponseDto exist_user = userService.getByEmail(email);
        if (exist_user != null) {
            customUserDetails.setUsername(exist_user.getUsername());
        } else {
            String username = generateUniqueUsername(email, user_id);
            customUserDetails.setUsername(username);
        }

        customUserDetails.setEmail(email);
        String username = generateUniqueUsername(email, user_id);
        customUserDetails.setUsername(username);

        return customUserDetails;
    }

    @Override
    public boolean accepts(OAuth2UserRequest userRequest) {
        return AuthProvider.VK
                .name()
                .equalsIgnoreCase(userRequest.getClientRegistration().getRegistrationId());
    }

    private String retrieveAttr(String attr, List<Map<String, Object>> response) {
        return response.stream()
                .findFirst()
                .map(attrs -> attrs.getOrDefault(attr, "").toString())
                .orElse("");
    }

    private String generateUniqueUsername(String email, String user_id) {
        String username_base = email.split("@")[0];
        String username_suffix;
        Set<String> exist_usernames = userService.findUsernameSetByUsername(username_base);

        byte i = 0;
        do {
            if (i <= user_id.length()) {
                username_suffix = user_id.substring(0, i);
                i++;
            } else {
                username_suffix = RandomStringUtils.randomAlphanumeric(6, 8);
            }
        } while (exist_usernames.contains(username_base + username_suffix));
        return username_base + username_suffix;
    }
}
