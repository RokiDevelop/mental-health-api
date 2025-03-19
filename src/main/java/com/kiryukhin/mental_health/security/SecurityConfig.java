package com.kiryukhin.mental_health.security;

import com.kiryukhin.mental_health.security.jwt.JwtAccessDeniedHandler;
import com.kiryukhin.mental_health.security.jwt.JwtAuthenticationEntryPoint;
import com.kiryukhin.mental_health.security.jwt.JwtFilter;
import com.kiryukhin.mental_health.security.oauth.CustomOAuth2UserService;
import com.kiryukhin.mental_health.security.oauth.OAuth2AuthenticationFailureHandler;
import com.kiryukhin.mental_health.security.oauth.OAuth2AuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true, prePostEnabled = true, securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${frontend.base-url}")
    private String FRONTEND_BASE_URL;

    private final JwtFilter jwtFilter;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final DatabaseLoginSuccessHandler databaseLoginSuccessHandler;
    private final CustomLogoutSuccessHandler customLogoutSuccessHandler;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final AuthenticationFailureHandler authenticationFailureHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .headers(x -> x.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(
                        handlingConfigurer -> {
                            handlingConfigurer.accessDeniedHandler(jwtAccessDeniedHandler);
                            handlingConfigurer.authenticationEntryPoint(jwtAuthenticationEntryPoint);
                        })
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/admin", "/admin/**").hasRole("ADMIN")
                        .requestMatchers("/roles", "/roles/**").hasRole("ADMIN")
                        .requestMatchers("/users", "/users/**").hasRole("ADMIN")

                        .requestMatchers("/meetings/group-practice/**").permitAll()
                        .requestMatchers("/meetings/individual-consultation/**").authenticated()

                        .requestMatchers("/video-courses/**").permitAll()
                        .requestMatchers("/audio-courses/**").permitAll()
                        .requestMatchers("/course-tags/**").permitAll()
                        .requestMatchers("/course-topics/**").permitAll()

                        .requestMatchers("/files**").permitAll()

                        .requestMatchers(
                                HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/public/**",
                                "/error", "/error/**",
                                "/auth/**", "/oauth2/**", "/verification/**").permitAll()
                        .anyRequest().authenticated())

                .formLogin(form -> {
                    form.loginProcessingUrl("/auth/login")
                            .successHandler(databaseLoginSuccessHandler)
                            .failureHandler(authenticationFailureHandler)
                            .permitAll();
                })
                .logout(x -> {
                    x.logoutUrl("/auth/logout");
                    x.logoutSuccessHandler(customLogoutSuccessHandler);
                })
                .oauth2Login(
                        x -> {
                            x.userInfoEndpoint(y -> y.userService(customOAuth2UserService));
                            x.successHandler(oAuth2AuthenticationSuccessHandler);
                            x.failureHandler(oAuth2AuthenticationFailureHandler);
                        })
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(withDefaults())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        SecurityFilterChain chain = httpSecurity.build();
        log.info("Configured security filter chain: {}", chain);
        return chain;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.setAllowedOrigins(List.of(FRONTEND_BASE_URL));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfiguration.setAllowedHeaders(List.of("*"));
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }
}
