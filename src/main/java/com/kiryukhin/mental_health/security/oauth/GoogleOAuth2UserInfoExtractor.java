package com.kiryukhin.mental_health.security.oauth;

import com.kiryukhin.mental_health.models.AuthProvider;
import com.kiryukhin.mental_health.security.CustomOauthUserDetails;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class GoogleOAuth2UserInfoExtractor implements OAuth2UserInfoExtractor {

    @Override
    public CustomOauthUserDetails extractUserInfo(OAuth2User oAuth2User) {
        CustomOauthUserDetails customUserDetails = new CustomOauthUserDetails();
        customUserDetails.setUsername(
                retrieveAttr("email", oAuth2User).split("@")[0]
                        .concat(retrieveAttr("sub", oAuth2User).substring(0, 6)));
        customUserDetails.setName(retrieveAttr("name", oAuth2User));
        customUserDetails.setEmail(retrieveAttr("email", oAuth2User));
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
}
