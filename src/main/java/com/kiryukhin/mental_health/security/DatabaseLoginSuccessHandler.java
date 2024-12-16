package com.kiryukhin.mental_health.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kiryukhin.mental_health.dtos.requests.TokenDto;
import com.kiryukhin.mental_health.servicesLogic.TokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DatabaseLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenService tokenService;
    private final UserDetailsServiceImpl userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        userService.updateAuthProvider(userDetails.getUsername(), "LOCAL");
        super.onAuthenticationSuccess(request, response, authentication);
    }

    @Override
    protected void handle(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {

        if (authentication.getPrincipal() instanceof CustomUserDetails user) {
            TokenDto token = tokenService.generateTokenPairs(user.getUsername());

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

            Map<String, String> data = new HashMap<>();
            data.put("access_token", accessToken);

            ObjectMapper objectMapper = new ObjectMapper();
            response.getWriter().write(objectMapper.writeValueAsString(data));
        } else {
            throw new IllegalArgumentException("Unexpected principal type: " + authentication.getPrincipal().getClass());
        }
    }
}