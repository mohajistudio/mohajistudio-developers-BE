package com.mohajistudio.developers.authentication.config;

import com.mohajistudio.developers.authentication.filter.JwtAuthFilter;
import com.mohajistudio.developers.authentication.handler.JwtAccessDeniedHandler;
import com.mohajistudio.developers.authentication.handler.JwtAuthenticationEntryPoint;
import com.mohajistudio.developers.authentication.service.CustomUserDetailsService;
import com.mohajistudio.developers.authentication.util.JwtUtil;
import com.mohajistudio.developers.database.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    private static final String AUTHORITY_ADMIN = Role.ROLE_ADMIN.name();
    private static final String AUTHORITY_DEVELOPER = Role.ROLE_DEVELOPER.name();
    private static final String AUTHORITY_GUEST = Role.ROLE_GUEST.name();

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorizeRequests -> {
            // ALL
            authorizeRequests.requestMatchers(HttpMethod.POST, "/auth/register/email/request", "/auth/register/email/verify", "/auth/register/status").permitAll();

            // GUEST
            authorizeRequests.requestMatchers(HttpMethod.POST, "/auth/register/password", "/auth/register/nickname").hasAuthority(AUTHORITY_GUEST);
            authorizeRequests.anyRequest().authenticated();
        });

        http.exceptionHandling(exceptionHandling -> {
            exceptionHandling.authenticationEntryPoint(jwtAuthenticationEntryPoint);
            exceptionHandling.accessDeniedHandler(jwtAccessDeniedHandler);
        });

        http.addFilterBefore(new JwtAuthFilter(customUserDetailsService, jwtUtil), UsernamePasswordAuthenticationFilter.class);
        http.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.cors(AbstractHttpConfigurer::disable);
        http.csrf(AbstractHttpConfigurer::disable);
        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}

