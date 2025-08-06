package com.backend.ToDoList.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;
    @Bean
    public AuthTokenFilter authTokenFilter() {
        return new AuthTokenFilter();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,AuthTokenFilter authTokenFilter) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(e ->
                        e.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/auth/login","/auth/verifyOtp","/auth/forgotPassword"
                                        , "/auth/register","/auth/googleLogin","/auth/resendEmail",
                                        "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .anyRequest().authenticated()
                );

        http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedHeader("*");

        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsFilter(urlBasedCorsConfigurationSource);
    }
}
