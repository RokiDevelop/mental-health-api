package com.kiryukhin.mental_health.security.oauth;

import com.kiryukhin.mental_health.security.CustomOauthUserDetails;
import com.kiryukhin.mental_health.services.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private TokenService tokenService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        handle(request, response, authentication);
        super.clearAuthenticationAttributes(request);
    }

    @Override
    protected void handle(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        String targetUrl = "http://localhost:8080/oauth2/redirect-success";
        CustomOauthUserDetails user = (CustomOauthUserDetails) authentication.getPrincipal();
        String token = tokenService.generateAccessToken(user.getUsername());
        targetUrl =
                UriComponentsBuilder.fromUriString(targetUrl)
                        .queryParam("token", token)
                        .build()
                        .toUriString();
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
