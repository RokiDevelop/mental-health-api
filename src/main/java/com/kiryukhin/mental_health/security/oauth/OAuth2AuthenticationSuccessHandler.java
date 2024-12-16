package com.kiryukhin.mental_health.security.oauth;

import com.kiryukhin.mental_health.dtos.requests.TokenDto;
import com.kiryukhin.mental_health.security.CustomOauthUserDetails;
import com.kiryukhin.mental_health.security.UserDetailsServiceImpl;
import com.kiryukhin.mental_health.servicesLogic.TokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${frontend.base-url}")
    private String frontendBaseUrl;
    private final TokenService tokenService;
    final private UserDetailsServiceImpl userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        handle(request, response, authentication);
        CustomOauthUserDetails userDetails = (CustomOauthUserDetails) authentication.getPrincipal();
        userService.updateAuthProvider(userDetails.getUsername(), userDetails.getProvider().name());
        super.clearAuthenticationAttributes(request);
    }

    @Override
    protected void handle(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        String targetUrl = frontendBaseUrl + "callback_success";

        if (authentication.getPrincipal() instanceof CustomOauthUserDetails user) {
            TokenDto token = tokenService.generateTokenPairs(user.getEmail());

            String accessToken = token.getAccessToken();
            String refreshToken = token.getRefreshToken();

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");

            Cookie refreshCookie = new Cookie("refresh_token", refreshToken);
            refreshCookie.setHttpOnly(true);
            refreshCookie.setSecure(true);
            refreshCookie.setPath("/api");
            refreshCookie.setMaxAge(60 * 60 * 24 * 7);
            response.addCookie(refreshCookie);

            targetUrl = UriComponentsBuilder.fromUriString(targetUrl)
                    .queryParam("access_token", accessToken)
                    .build()
                    .toUriString();

            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        } else {
            throw new IllegalArgumentException("Unexpected principal type: " + authentication.getPrincipal().getClass());
        }
    }
}