package com.mohajistudio.developers.authentication.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohajistudio.developers.authentication.service.AuthenticationService;
import com.mohajistudio.developers.authentication.service.CustomUserDetailsService;
import com.mohajistudio.developers.authentication.util.JwtUtil;
import com.mohajistudio.developers.common.enums.ErrorCode;
import com.mohajistudio.developers.common.exception.CustomException;
import com.mohajistudio.developers.database.dto.UserDto;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final AuthenticationService authenticationService;
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
                    /// 로그아웃된 사용자 체크
                    long issuedAt = (Long) claims.get("iat");

                    ObjectMapper mapper = new ObjectMapper();
                    UserDto user = mapper.convertValue(claims.get("user"), UserDto.class);

                    Long logoutTime = authenticationService.getLogoutTime(user.getId());
                    if (logoutTime != null && issuedAt < logoutTime) {
                        throw new CustomException(ErrorCode.INVALID_ACCESS_TOKEN);
                    }

                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());

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

        String code = ErrorCode.UNKNOWN_ERROR.getCode();
        String message = e.getMessage();

        log.error(e.getMessage(), e);

        String jsonResponse = String.format("{\"code\": \"%s\", \"message\": \"%s\"}", code, message);
        response.getWriter().write(jsonResponse);
    }
}
