package com.kiryukhin.mental_health.security.oauth;

import com.kiryukhin.mental_health.models.AuthProvider;
import com.kiryukhin.mental_health.security.CustomOauthUserDetails;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class VkOAuth2UserInfoExtractor implements OAuth2UserInfoExtractor {

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

        String user_id;
        if (userRequestAttrs.get("user_id") instanceof String) {
            user_id = (String) userRequestAttrs.get("user_id");
        } else if (userRequestAttrs.get("user_id") instanceof Integer) {
            user_id = Integer.toString((Integer) userRequestAttrs.get("user_id"));
        } else {
            user_id = RandomStringUtils.randomAlphanumeric(6, 8);
        }

        customUserDetails.setEmail(email);
        customUserDetails.setUsername(email.split("@")[0].concat(user_id));

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
}
