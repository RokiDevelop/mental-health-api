package com.kiryukhin.mental_health.security.oauth;

import com.kiryukhin.mental_health.security.CustomOauthUserDetails;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface OAuth2UserInfoExtractor {

    CustomOauthUserDetails extractUserInfo(OAuth2User oAuth2User);

    CustomOauthUserDetails extractUserInfo(OAuth2User oAuth2User, OAuth2UserRequest oAuth2UserRequest);

    boolean accepts(OAuth2UserRequest userRequest);
}
