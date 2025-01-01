package com.mohajistudio.developers.authentication.filter;

import com.mohajistudio.developers.authentication.service.CustomUserDetailsService;
import com.mohajistudio.developers.authentication.util.JwtUtil;
import com.mohajistudio.developers.common.enums.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7);

                Map<String, Object> claims = jwtUtil.extractPayload(token);
                if (claims != null) {
                    String email = (String) claims.get("email");

                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

                    if (userDetails != null) {
                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    }
                }
            }

            filterChain.doFilter(request, response);
        } catch (JwtException ex) {
            handleJwtException(response, ex);
        } catch (Exception e) {
            handleException(response, e);
        }
    }

    // JWT 예외 처리 메서드
    private void handleJwtException(HttpServletResponse response, JwtException e) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        String code = ErrorCode.INVALID_ACCESS_TOKEN.getCode();
        String message = e.getMessage();

        String jsonResponse = String.format("{\"code\": \"%s\", \"message\": \"%s\"}", code, message);
        response.getWriter().write(jsonResponse);
    }

    private void handleException(HttpServletResponse response, Exception e) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        String code = ErrorCode.INVALID_ACCESS_TOKEN.getCode();
        String message = ErrorCode.INVALID_ACCESS_TOKEN.getMessage();

        String jsonResponse = String.format("{\"code\": \"%s\", \"message\": \"%s\"}", code, message);
        response.getWriter().write(jsonResponse);
    }
}
