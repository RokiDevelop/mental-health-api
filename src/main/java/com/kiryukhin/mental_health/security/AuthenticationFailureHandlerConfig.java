package com.kiryukhin.mental_health.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFailureHandlerConfig {
    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return (request, response, exception) -> {
            String errorMessage;
            if (exception instanceof BadCredentialsException) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                errorMessage = "BadCredentialsException";
            } else if (exception instanceof UsernameNotFoundException) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                errorMessage = "UsernameNotFoundException";
            } else if (exception instanceof DisabledException) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                errorMessage = "DisabledException";
            } else if (exception instanceof LockedException) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                errorMessage = "LockedException";
            } else {
                errorMessage = "AuthenticationFailed";
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"" + errorMessage + "\"}");
        };
    }
}
